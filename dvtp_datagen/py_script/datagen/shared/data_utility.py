'''
Created on 26 Sep 2012

@author: epstvxj
'''
from datagen.settings import datagen_logger
from datagen.shared.constants import SGEH_EVENT_ATTACH, SGEH_EVENT_ACTIVATE, \
    SGEH_EVENT_RAU, SGEH_EVENT_ISRAU, SGEH_EVENT_DEACTIVATE, SGEH_EVENT_DETACH, \
    SGEH_EVENT_SERVICE_REQUEST, SGEH_EVENT_RECORD, SGEH_ERROR_RECORD, \
    SGEH_FOOTER_RECORD
from struct import unpack
import binascii
import gzip

def get_sgeh_events_count_map(sgeh_file):
    
    if sgeh_file is None:
        return None
    
    # create a map with all supported sgeh events and their corresponding count (default to 0)
    event_count_map = {SGEH_EVENT_ATTACH:0,
                       SGEH_EVENT_ACTIVATE:0,
                       SGEH_EVENT_RAU:0,
                       SGEH_EVENT_ISRAU:0,
                       SGEH_EVENT_DEACTIVATE:0,
                       SGEH_EVENT_DETACH:0,
                       SGEH_EVENT_SERVICE_REQUEST:0}
    
    with open(sgeh_file, 'rb') as reader:
        event_count = 0
                        
        _original_bytes = reader.read(12) # skip 12 bytes
        original_bytes = reader.read(2)                
        while original_bytes:
            # event length
            byte_list = unpack('2B', original_bytes)
            record_length = byte_list[0] << 8
            record_length |= byte_list[1]
            
            # event type
            original_bytes = reader.read(1)
    
            # record type
            record_type = -1
            byte_list = unpack('B', original_bytes)    
            if byte_list[0] == 1:
                record_type = SGEH_EVENT_RECORD
            elif byte_list[0] == 2:
                record_type = SGEH_ERROR_RECORD
            elif byte_list[0] == 3:
                record_type = SGEH_FOOTER_RECORD
            
            # core events
            if record_type == SGEH_ERROR_RECORD:
                original_bytes = reader.read(5)                
            elif record_type == SGEH_FOOTER_RECORD:
                original_bytes = reader.read(1)                        
            elif record_type == SGEH_EVENT_RECORD:
                original_bytes = reader.read(record_length-3)
                byte_list = unpack(''.join(['>',str(record_length-3),'B']), original_bytes)                            
                original_event_id = byte_list[0] 
                                     
                if original_event_id == SGEH_EVENT_RAU:       
                    event_count_map[SGEH_EVENT_RAU] += 1                                                   
                elif original_event_id == SGEH_EVENT_ATTACH:
                    event_count_map[SGEH_EVENT_ATTACH] += 1
                elif original_event_id == SGEH_EVENT_ACTIVATE:
                    event_count_map[SGEH_EVENT_ACTIVATE] += 1
                elif original_event_id == SGEH_EVENT_ISRAU:
                    event_count_map[SGEH_EVENT_ISRAU] += 1
                elif original_event_id == SGEH_EVENT_DEACTIVATE:
                    event_count_map[SGEH_EVENT_DEACTIVATE] += 1                    
                elif original_event_id == SGEH_EVENT_DETACH:
                    event_count_map[SGEH_EVENT_DETACH] += 1
                elif original_event_id == SGEH_EVENT_SERVICE_REQUEST:
                    event_count_map[SGEH_EVENT_SERVICE_REQUEST] += 1                    
                event_count += 1  
                           
            original_bytes = reader.read(2)    
    
    return event_count_map

def get_ggsn_events_count_map(ggsn_file):
    
    if ggsn_file.endswith('bin.gz'):
        mpN_reader = gzip.open(ggsn_file, 'rb')
    else:
        mpN_reader = open(ggsn_file, 'rb')
            
    event_count_map = {}
    while True:
        try:                
            # record length
            original_bytes = mpN_reader.read(2)                    
            if not original_bytes:
                # end of file reached
                break 
            
            record_length = int(binascii.hexlify(original_bytes),base=16)
            
            original_bytes = mpN_reader.read(record_length-2)
                                        
            byte_list = unpack(''.join(['>',str(record_length-2),'B']), original_bytes)
                            
            # bit offset of event id
            offset = 61            
            if (record_length-2)*8 < 61+10:
                continue
            
            original_unpacked_byte_list = get_value(10, (8-offset%8), offset, byte_list)[2]
            
            original_event_id = get_integer_value_from_byte_list(original_unpacked_byte_list, 10)
            
            if original_event_id not in event_count_map:
                event_count_map[original_event_id] = 0
            event_count_map[original_event_id] += 1
            
        except:                    
            break
        finally:
            mpN_reader.close()
            
    return event_count_map

def validate_imsi(original_imsi_bytearray, enriched_imsi_bytearray, enriched_imsi_prefix, original_imsi_prefix, event_id):
    
    original_imsi = str(get_byte_array_tbcd_string(original_imsi_bytearray, 8))
    enriched_imsi = str(get_byte_array_tbcd_string(enriched_imsi_bytearray, 8))
    
    if original_imsi_prefix != original_imsi[0:len(original_imsi_prefix)]:
        return
    
    prefix_len = len(enriched_imsi_prefix)
    if enriched_imsi[0:prefix_len] != enriched_imsi_prefix:
        datagen_logger.error('imsi prefix does not match: %s != %s [enriched imsi %s] [%d]', enriched_imsi_prefix, enriched_imsi[0:prefix_len], enriched_imsi, event_id)
    
    original_suffix = str(original_imsi)[prefix_len:]
    enriched_suffix = str(enriched_imsi)[prefix_len:]
    
    if original_suffix != enriched_suffix:
        datagen_logger.error('imsi suffix does not match: %s != %s [original imsi %s] [%d]', original_suffix, enriched_suffix, original_imsi, event_id)
    
def get_imsi(offset, byte_list):
    remaining_bits_in_byte = 8 - offset % 8 
    remaining_required_bits = 64
    return_value = get_value(remaining_required_bits, remaining_bits_in_byte, offset, byte_list)
    imsi = return_value[2]
    
    return imsi

def get_byte_array_ibcd(value, number_of_bits):
    
    return_value_list = []
    index = 0
    i = 0
    while index < number_of_bits/4:
        return_value_list.append(value[i] & 0x0F)
        return_value_list.append((value[i] & 0xF0) >> 4)
        index += 2
        i += 1
    return return_value_list

def get_value(remaining_required_bits, remaining_bits_in_byte, offset, byte_list):
    
    value = []
    index = 0
        
    range_value = remaining_required_bits/8+1 if remaining_required_bits % 8 > 0 else remaining_required_bits / 8
    while range_value != 0:        
        value.append(0)
        range_value -= 1
    
    working_bits = 0
    missing_bits = 8 if remaining_required_bits > 8 else remaining_required_bits
    while remaining_required_bits > 0:                        
        
        working_bits = remaining_bits_in_byte if remaining_bits_in_byte < missing_bits else missing_bits
            
        byte_index = offset/8
        offset += working_bits
        number_of_bits_to_shift = remaining_bits_in_byte - working_bits 
        
        mask = get_mask_with_ones(working_bits) << number_of_bits_to_shift

        tmp = (byte_list[byte_index] & mask)
        
        number_of_bits_to_shift = remaining_bits_in_byte - working_bits  
        tmp >>= number_of_bits_to_shift
        value[index] |= tmp 
        
        remaining_bits_in_byte -= working_bits
        remaining_required_bits -= working_bits
        
        missing_bits -= working_bits
        if missing_bits > 0:
            value[index] <<= missing_bits
        else:
            missing_bits = 8 if remaining_required_bits > 8 else remaining_required_bits
            index += 1
        
            
        if remaining_bits_in_byte == 0:
            remaining_bits_in_byte = 8        
            
    return (offset, remaining_bits_in_byte, value)

    
def get_byte_array_tbcd_string(byte_list, number_of_bytes):    
    value = 0
    for index in range(number_of_bytes):     
        if (byte_list[index] % 16 != 15):
            value = (byte_list[index] % 16) + (value * 10);
        if (byte_list[index] / 16 != 15):
            value = (byte_list[index] / 16) + (value * 10);
    return value;

def get_mask_with_ones(number_of_bits):
    mask = 0
    for i in range(number_of_bits):
        mask += pow(2,i)
    if mask == 0:
        return 0xFF
    return mask

def get_integer_value_from_byte_list(byte_list, number_of_bits):
    
    number_of_bytes = number_of_bits / 8
    remaining_bits = number_of_bits % 8
    
    value = 0L
    for index in range(number_of_bytes):
        value <<= 8
        value |= byte_list[index]
        
    if remaining_bits > 0:
        value <<= remaining_bits
        value |= byte_list[number_of_bytes]
    
    return value