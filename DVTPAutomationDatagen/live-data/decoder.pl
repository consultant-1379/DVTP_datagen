#!/usr/bin/perl


#
# File      : parse_ebm_log.pl 
# Purpose   : Parse EBM logs for troubleshooting purposes.
# updated by    : EWENHAN + EENZCHA + ERVCARL
# Date      : 2011-03-23
#
# Note: -r flag is at present not applicable for CPG. Flag is not listed in on-line help,
# but is still part of the code since it may become applicable later on.
#

require 5.6.1;
use Getopt::Std;
use strict;
use POSIX;
use File::Basename;
use Cwd;
use Symbol;
##The directory where this script is located
use FindBin '$Bin';

my $xml_file = "./ebm.xml";
my $cpg_xml_file = "$Bin/ebm_event_specification.xml";

my $cause_xml = "./ebm_cause_codes.xml";

my $OUT = Symbol::gensym();;
$OUT =  \*STDOUT;
local *FH;
my($TTX_OS_HW)=`/tmp/DPE_SC/Scripts/dpe_uname -D`;chomp($TTX_OS_HW);
my($CP,$TAIL)=("cp","tail");
my $prog=basename $0;
my $ebs_log_binary_DIR = "/tmp/OMS_LOGS/ebs/ready";
my $ebs_log_binary;
my $ebs_file_inarg; 
my $total_content;

##Current EBM record
my $this_record_content_BIT;
my $this_record_content_BIT_index;
my $this_record_length; 
my $this_record_length_actual;
my $this_record_content;
my %event_record;

##Arguments
my %opts;
my $rat_opts;
my $event_type_opts;
my $summary = "false";
my $version_differ = "false";
my $log_ffv;
my $log_fiv;

##Header
my ($FFV,$FIV,$year,$month,$day,$hour,$minute,$second,$utc_offset,$cause_header,$nodeid);

##Format
my $delimiter = "\n";
my $pr_all=0;

##Counters
my $nbr_filtered_stream_headers = 0;
my $nbr_unknown_event = 0;
my $total_nbr_events = 0;

##Filters
my @filters;
my %filters;
my $gsm = "false";
my $wcdma = "false";
my $unsuccessful = "false";
my $unauthenticated = "false";
my $imsi = "false";
my $imsi_pattern = "";
my $tac ="false";
my $tac_pattern = "";
my $cause_code = "false";
my $cause_code_pattern = "";
my $apn = "false";
my $apn_pattern = "";
my $trigger = "false";
my $trigger_pattern = "";
my $decode_all = "true";

## hash tables for xml parse
## cause code
my %cause_code = ();
## enums
my $enums = {};

## decode function for each IE type
my %decode_functions = (
            'uint' => \&decode_uint,
            'enum' => \&decode_enum,
            'bytearray' => \&decode_bytearray,
            'ipaddress' => \&decode_ipaddress,
            'ipaddressv4' => \&decode_ipaddressv4,
            'ipaddressv6' => \&decode_ipaddressv6,          
            'dnsname' => \&decode_dnsname,
            'tbcd' => \&decode_tbcd,
            'ibcd' => \&decode_ibcd);
            
my $print_event = [];
# // This is the structure used for printing
# $print_event = [ hashRef , hashRef, hashRef ... ] // ordered list of all possible elements in any event
#                  hashRef{key} => scalar // if the element is a parameter type it is placed in a scalar, the scalar is undef if no value exists
#                  hashRef{key} => arrayRef // if the element is a struct, the key ("$el_name+$type") gives an array reference
#                  arrayRef[ hashRef, hashRef, hashRef ... ] // see above
#                  hashRef{key} => arrayRef // if the element is a struct with the seqmaxlen attribute, the key gives an array reference with each
#                                           // possible record
#
my $events = {};
# // This is the structure used for parsing
# $events = { id => { 
#                       event_name = $event_name,
#                       elements => [ 
#                                   { el_name => $el_name, seq_max_len => $seq_max_len, seq_len =>$seq_len, structs => "array ref to an array of refs to structure_types entries" },
#                                   { el_name => $el_name, use_valid => $use_valid, optional => $optional, param => "hash ref to an parameter_types entry" },
#                                   ]       
#                   }
#           }


my ($wId, $sec);
my @working= qw(- \ | /);

#####################################################
sub print_caution {
    my $sep = "\t" . '!' x 60 . "\n\n";
    print $OUT $sep;
    print $OUT "\tCAUTION!\tCAUTION!\tCAUTION!\tCAUTION!\n\n";
    print $OUT "\t$prog is not intended to be run on the CPG node, it might impact the capacity of the CPG.\n";
    print $OUT "\tRunning this command may cause heavy CPU load, depending on the size of the input file.\n";
    print $OUT "\tPlease install the script on another UNIX machine, where EBM logs are collected.\n";
    print $OUT $sep; 
}

sub print_summary {
    print $OUT "\n\tThis program can be used to parse EBM logs. The file ebm_event_specification.xml should be \n";
    print $OUT "\tput in the same directory as $prog.\n";
}

sub print_usage {
    print $OUT "\n\tUsage: \n";
    print $OUT "\n\t$prog [-h | -s | [-f logfile] [-d directory] [-o outfile] [-S] [-l] [-p delimiter] \n";
    print $OUT "\t\t[-u] [-e 'event_type[,...]'] [-v 'event_trigger[,...]'] [-c cause_code[,...]]\n";
    print $OUT "\t\t[-i imsi-number[,...]] [-n] [-t tac[,...]] [-a apn[,...]]]  \n";
}

#####################################################
sub HELP_MESSAGE {
    my $sep = "\t" . '-' x 50 . "\n\n";
    my $pr = 'user@host > ';

    &print_summary;
    &print_usage;
    print $OUT "\n\t-h: Displays the EBM decoder help text.\n";
    print $OUT "\n\t-s: Displays information about the EBM decoder.\n";
        priot $OUT "\n\t-x: Use this protocol specification (.xml)\n";
    print $OUT "\n\t-f: Specifies the EBM log file (with absolute path) to parse. If the EBM log file is not specified, \n";
    print $OUT "\t\a $prog parses all the log files in the current working directory or the directory  \n";
    print $OUT "\t\a specified using option -d.\n";
    print $OUT "\n\t-d: Specifies the directory where EBM log files are located. If no directory is specified, \n";
    print $OUT "\t\a the log files in current working directory are parsed. \n";
    print $OUT "\n\t-o: Specifies the output file. The output is displayed in the console if no output file is specified. \n";
    print $OUT "\n\t-S: Displays a summary of found events in the parsed log files. \n";
    print $OUT "\n\t-l: Print as table. If option -p is not specified, default delimiter ; is used. \n";
    print $OUT "\n\t-p: Overwrites default delimiter. Option -l must be specified for option -p to work. \n";
    print $OUT "\n\t-u: Filter by unsuccessful events.\n";
    print $OUT "\n\t-e: Filter by the event type specified. * can be used to match any character. \n";      
    print $OUT "\n\t-v: Filter by the event trigger specified. * can be used to match any character. \n";
    print $OUT "\n\t-c: Filter by cause code (decimal value) specified. \n";
    print $OUT "\n\t-i: Filter by IMSI number specified.\n";
    print $OUT "\n\t-n: Filters unauthenticated IMSI numbers.\n";
    print $OUT "\n\t-t: Filter by Type Approval Code (TAC) specified. TAC is the first eight digits in IMEI.\n";
    print $OUT "\n\t-a: Filter by Access Point Name (APN) specified. \n";

    print $OUT $sep;
    print $OUT "\tExamples:\n";
    print $OUT $sep;
    print $OUT "\tFilter on unsuccessful events\n";
    print $OUT "\t$pr$prog -u -f /tmp/A20081119.0541+0100-20081119.0542+0100_1_ebmc.28\n\n";
    print $OUT $sep;
    print $OUT "\tFilter by Create Session Events and All Events Ending with Deletion\n";
    print $OUT "\t$pr$prog -e 'session_creation,*deletion' -f /tmp/A20081119.0541+0100-20081119.0542+0100_1_ebmc.28\n\n";
    print $OUT $sep;
    print $OUT "\tFilter by IMSI Number 240999800416785 and 240999802602314\n";
    print $OUT "\t$pr$prog -i 240999800416785,240999802602314  -f /tmp/A20081119.0541+0100-20081119.0542+0100_1_ebmc.28\n\n";
    print $OUT $sep;
    print $OUT "\tEBM summary for All Logs Located in the Directory /tmp/ebs_logs.\n";
    print $OUT "\t$pr$prog -S -d /tmp/ebs_logs\n\n";
    exit 0;
}

#####################################################
sub logg {
    my ($msg_type,$msg_txt)=@_;
    my ($sec,$min,$hour,$day,$mon,$year)=localtime;
    my $date=sprintf("%d-%02d-%02d %02d:%02d:%02d",$year+1900,$mon+1,$day,$hour,$min,$sec);
    local $_=$msg_type;

    SWITCH: {
        /^info/     && do
            {
                print $OUT  ( "$date\tINFO:\t$msg_txt\n");
            };
        /^warn/ && do
            {
                print $OUT  ("$date\tWARNING:\t$msg_txt\n");
            };
        /^error/    && do
            {
                print $OUT ( "$date\tERROR:\t$msg_txt\n");
                exit 1;
            };
        /^output/       && do
            {
                print $OUT  ( "$date\tINFO:\t$msg_txt\n");
            };
        /^nolog_error/  && do
            {
                print("\tERROR:\t$msg_txt\n");
                exit 1;
            };
        /^no_format/    && do
            {
                print("$msg_txt");
            };
    }
}

#####################################################
sub modify_opt {
    my ($pattern)=@_;
    my $ret=" ";
    my @arr=split(/\s*\,\s*|\s+/,$pattern);
    foreach my $e (@arr) {
        $ret .= $e . " ";
    }
    return $ret;
}

#####################################################
# It's used to rebuild the regular expression (change * to .*).
#####################################################
sub build_event_list {
    my ($event) = @_;
    my $matched = 0;
    $event =~  s/\*/\.\*/g; 

    foreach my $key (keys %{$events}){
    if ( $events->{$key}{event_name} =~ /^$event/i){
            $events->{$key}{show} = 1;
            $matched = 1;
        }
    }

    if ($matched == 0){
        print("\nError: Event type option \"$event\" can't match any event defined in ebm.xml/ebm_event_specification.xml. Defined events:\n");
        foreach my $key (keys %{$events}){
            print "\t$events->{$key}{event_name}\n";
        }
        print(" \nNote: * can be used as wildcard, last asterisk can be obmitted. Not case-sensitive.\n");
        print("Example: \"$prog -e l_d*_a\" to filter event \"L_DEDICATED_BEARER_ACTIVATE\".\n\n");
        exit 0;
    }
}

#####################################################
# Check that the event trigger to filter for exists, then add it to pattern
#####################################################
sub build_trigger_pattern {
    my ($trigger) = @_;
    my $matched = 0;
    $trigger =~  s/\*/\.\*/g;   

    foreach my $enum_code (keys %{$enums->{event_trigger}}){
        my $enum_val = $enums->{event_trigger}->{$enum_code};
        if ($enum_val =~ /^$trigger/i){                     
            $trigger_pattern .= $enum_val." ";
            $matched = 1;
        }
    }
        
    if ($matched == 0){
        print("\nError: Event trigger option \"$trigger\" can't match any trigger defined in ebm.xml/ebm_event_specification.xml. Defined triggers:\n");

        foreach my $enum_code (keys %{$enums->{event_trigger}}){
            my $enum_val = uc($enums->{event_trigger}->{$enum_code});
            print "\t$enum_val\n";
        }
        print(" \nNote: * can be used as wildcard, last asterisk can be obmitted. Not case-sensitive.\n");
        print("Example: \"$prog -v b*_a\" to filter trigger \"BEARER_ACTIVATION\".\n\n");
        exit 0;
    }
}

#####################################################
# It's used to rebuild the regular expression (change * to .*).
#####################################################
sub get_opts {
    if (exists $opts{'S'}) {
        $summary="true";
    } else {
        if (exists $opts{'l'}) {
            $pr_all= 1;
            $delimiter=';';
        }
        # this parameter is only applicable for table output
        if (exists $opts{'p'} && $pr_all == 1) {
            if(length($opts{'p'})>0){
                $delimiter=$opts{'p'}; 
            }else{
                $delimiter=';';
            }
        } 
        if (exists $opts{'u'}) {
            $unsuccessful="true";
        } 
        if (exists $opts{'n'}) {
            $unauthenticated="true";
        } 
        if (exists $opts{'r'}) {
            $rat_opts=['gsm','wcdma'];
            if ($opts{'r'}) {
                $opts{'r'}=~ s/^\s+//;
                my @arr=split(/\s*\,\s*|\s+/,$opts{'r'});
                foreach my $e (@arr) {
                    if (not (grep(/^$e$/i,@$rat_opts))) {
                        print("Error: Radio access type option: $e. Radio access type option can be one of gsm and wcdma. Exiting...\n");
                        exit 0;
                    } elsif ( $e eq "gsm") {
                        $gsm = "true";
                    } elsif ( $e eq "wcdma") {
                        $wcdma = "true";
                    }
                }
            }
        }
        if (exists $opts{'e'}) {
            $decode_all = "false";
            if ($opts{'e'}) {
                $opts{'e'}=~ s/^\s+//;
                my @arr=split(/\s*\,\s*|\s+/,$opts{'e'});
                foreach my $e (@arr)    {
                    &build_event_list($e);
                }
            }
        }
        if (exists $opts{'v'}) {
            if ($opts{'v'}) {
                $trigger = "true";
                $opts{'v'}=~ s/^\s+//;
                my @arr=split(/\s*\,\s*|\s+/,$opts{'v'});
                foreach my $e (@arr)    {
                    &build_trigger_pattern($e);
                }
            }
        }
        if (exists $opts{'c'}) {
            if ($opts{'c'}) {
                $cause_code = "true";
                $cause_code_pattern = modify_opt($opts{'c'});
            }
        }
        if (exists $opts{'i'}) {
            if ($opts{'i'}) {
                $imsi = "true";
                $imsi_pattern = modify_opt($opts{'i'});
            }
        }
        if (exists $opts{'t'}) {
            if ($opts{'t'}) {
                $tac = "true";
                $tac_pattern  = modify_opt($opts{'t'});
            }
        }

        if (exists $opts{'a'}) {
            if ($opts{'a'}) {
                $apn = "true";
                $apn_pattern = modify_opt($opts{'a'});
            }
        }
    }
    if (exists $opts{'x'}) {
        $xml_file = $opts{'x'};
        if (!-e $xml_file) { die "Error: File non existing: $xml_file\n"; }
    }
    if (exists $opts{'f'}) {
        $ebs_file_inarg = $opts{'f'};
    }
    &set_default_dir;
}

#####################################################
# Redirect the result into a file.
#####################################################
sub redirect {
    if ($opts{o}=~/\w+/) {
        if (! (open(FH,">> $opts{o}"))) {
            die ( "Failed to open log file $opts{o} - $!");
        }
        print $OUT "The output will be redirected into file: " .  $opts{o} . "\n";
        print $OUT "Please be patient! Parsing can take a while, depending on the size of input files!\n";
        $OUT=\*FH;
        print $OUT "# Created by $prog\n";
        #close(STDERR); #when redirecting the result, ERROR message still should be printed out.
    } else {
        $OUT=\*STDOUT;
    }
}

#####################################################
sub arraydir{
    my ($dir) = @_;
    opendir my $dh, $dir or logg ('warn',"can't opendir $dir - $!");;
    my @arr = ();
    while( my $file = readdir($dh) ) {
        next if $file =~ m[^\.{1,2}$];
        my $path = $dir .'/' . $file;
        push(@arr, $file) if -d $path;
    }
    return @arr;
}

#####################################################
sub set_default_dir {
    if (exists $opts{'d'}) {
        $ebs_log_binary_DIR = $opts{'d'};
    }else{
        my $snode=&is_running_on_node;
        if($snode){
            $ebs_log_binary_DIR="/tmp/OMS_LOGS/ebs/ready";
        }else{
            $ebs_log_binary_DIR=cwd() ;
        }
    }
}

#####################################################
# Parse Cause Code and Sub Cause Code defined in ebm_cause_codes.xml.
#####################################################
sub parse_cause {
    my ($parseing_params) = 0;
    my ($parseing_enumerations) = 0;
    my ($cause_name) = 0;
    my ($text,$code) ;
    if ( -r $cause_xml ){
        open(IN, "< $cause_xml") or logg('error',"Failed to open ebm_cause_codes.xml: $cause_xml - $!");
    }else {
        $cause_xml = "/tmp/DPE_SC/ApplicationData/GSN/ebm_cause_codes.xml";
        if ( -r $cause_xml ) {
            open(IN, "< $cause_xml") or logg('error',"Failed to open ebm_cause_codes.xml in current directory and node directory '/tmp/DPE_SC/ApplicationData/GSN/' - $!");
        } else {
            # For CPG: Read cause codes from ebm.xml
            open(IN, "< $cpg_xml_file") or logg('error',"Failed to open ebm_event_specification.xml in current directory' - $!");
        }
    }

    while (<IN>) {
        if ( /\<parametertype\>/) {
            $parseing_params = 1;
        }elsif ( /\<\/parametertype\>/) {
            $parseing_params = 0;
        }
        if ($parseing_params == 1){
            if ( /\<name\>(.*)\<\/name\>/ ) {
                # e.g. internal_cause_code
                $cause_name = lc($1);
            }elsif ( /\<enumeration\>/ ) {
                $parseing_enumerations = 1;
            }elsif ( /\<\/enumeration\>/) {
                $parseing_enumerations = 0;
            }   
            if($parseing_enumerations == 1){
                if (/internal.*=.*"(.*)".*value.*=.*"(.*)".*\>(.*)\<\/enum/ ) {                                 
                    $text = lc($1);
                    $code = $2;
                    $text =~ s/_/ /g;
                    $cause_code{$cause_name}{$code} = "#".$code."(".$text.")";      
                }
            }
        }
    }
    close(IN);
}

#####################################################
# Search if a key has a value in an array of hash refs
#####################################################
sub is_existing {
    my ($array_ref, $key) = @_;
    
    foreach my $hash_ref (@{$array_ref}) {
        if (exists $hash_ref->{$key}) {
            return 1;
        }
    }
    return 0;
}

#####################################################
# Return a hash ref if a key has a value in an array of hash refs
#####################################################
sub get_hash_ref {
    my ($array_ref, $key) = @_;
    
    foreach my $hash_ref (@{$array_ref}) {
        if (exists $hash_ref->{$key}) {
            return $hash_ref;
        }
    }
    return undef;
}

#####################################################
# Return a hash ref if a key has a value in an array of hash refs
# Will search among all sub arrays
#####################################################
sub find_hash_ref_value {
    my ($array_ref, $key) = @_;

    foreach my $hash_ref (@{$array_ref}) {
        foreach my $k (keys %$hash_ref) {
            #extract el_name from key 
            my ($el_name) = ($k =~ /(.*)\+.*/);
            my $value = $hash_ref->{$k};
            if ($el_name eq $key) {
                return $value;
            } elsif (ref($value) eq 'ARRAY') {
                my $ret = find_hash_ref_value($value, $key);
                return $ret if defined $ret;
            }
        }
    }
    return undef;
}

#####################################################
# This function creates the tree with ref to parameter types as leaves and
# ref to structs as branches 
#####################################################
sub add_all_included_elements {
    my ($s_types, $p_types, $elements, $print_struct) = @_;

    # for all elements included in the struct
    for my $i (0..$#{$elements} ) {
        my $elem = ${$elements}[$i]; 
        if ( exists $elem->{param} ) {
            my $key = "$elem->{el_name}+$elem->{type_name}";

            # if it is a parameter just copy the ref
            $elem->{param} = $p_types->{$elem->{type_name}};
            
            # add it to the print structure
            push(@{$print_struct}, { $key => undef });
        } elsif ( exists $elem->{struct} ) {
            my $key = "$elem->{el_name}+$elem->{type_name}+$i";
            
            # if it is a struct copy the array
            $elem->{struct} = $s_types->{$elem->{type_name}}{elements}; 

            # create a ref for the print structure
            my $struct = []; 
            push(@{$print_struct}, { $key => $struct });
            
            # if it is a struct then make a recursive call to find all included entries
            add_all_included_elements($s_types, $p_types, $elem->{struct}, $struct);
        } else {
            logg('error', "Could not add element in struct $elem->{el_name} $i!");
        }   
    }
}

#####################################################
# Parse all IE types and events (parameter and structure types and events).
#####################################################
sub parse_ebm_xml {
    my ($xmlfile)=@_;
    my $parseing_params = 0;
    my $parseing_structs = 0;
    my $parseing_events = 0;
    my $parameter_types = {};
    # $parameter_types = { name => { type => $type, len => $len, type_name => $type_name } ,
    #                      name => { // etc }       

    my $structure_types = {};
    # $structure_types = { name => { elements => [ 
    #                                   { el_name => $el_name, type_name => $parametertype_name, use_valid =>$use_valid, optional => $optional, param => "hash ref to an parameter_types entry" },
    #                                   { el_name => $el_name, type_name => $structuretype_name, struct => "array ref to structure_types entries" },
    #                                   ]       
    #                               }
    #                   }

    my $parametertype_name;
    my $param_type;
    my $struct_type;
    my $structuretype_name;
    my $event_name;
    my $id;
    my $el_name;
    my $type;
    my $len;
    my $use_valid;
    my $optional;
    my $seq_max_len;
    
    if (!-r $xmlfile) {
        $xmlfile = "/tmp/DPE_SC/ApplicationData/GSN/ebm.xml";       
    }
    if (!-r $xmlfile) {
        $xmlfile = $cpg_xml_file;
    }
    open(IN, "< $xmlfile") or logg('error', "Failed to open specification: $xmlfile - $!");
#   print "parse_ebm_xml: using specification: $xmlfile\n";

    while (<IN>) {
        if ( /\<ffv\>(.*)\<\/ffv\>/ ) {
            $FFV = $1;
        }elsif ( /\<fiv\>(.*)\<\/fiv\>/ ) {
            $FIV = $1;
        }elsif ( /\<parametertypes\>/) {
            $parseing_params = 1;
        }elsif ( /\<structuretypes\>/) {
            $parseing_structs = 1;
        }elsif ( /\<events\>/) {
            $parseing_events = 1;
        }elsif ( /\<\/parametertypes\>/) {
            $parseing_params = 0;
        }elsif ( /\<\/structuretypes\>/) {
            $parseing_structs = 0;
        }elsif ( /\<\/events\>/) {
            $parseing_events = 0;   
        }
        
        if ($parseing_params == 1){
            if ( /\<name\>(.*)\<\/name\>/ ) {
                $parametertype_name = lc($1);
                # "normalize" this label name, since it comes in different versions
                $parametertype_name =~ s/l_cause_prot_type|cause_prot_type/cause_protocol/;
            }elsif ( /\<type\>(.*)\<\/type\>/ ) {
                $type = lc($1);
            }elsif ( /\<numberofbits\>(.*)\<\/numberofbits\>/) {
                $len = $1;  
            }elsif ( /\<lengthbits\>(.*)\<\/lengthbits\>/) {
                $len = $1;  
            }elsif ( /\<\/parametertype\>/) {
                $parameter_types->{$parametertype_name}{type} = $type;
                $parameter_types->{$parametertype_name}{len} = $len;
                $parameter_types->{$parametertype_name}{type_name} = $parametertype_name;
            }elsif ( /internal\s*=\s*"(.*)" value\s*=\s*"(\d+)"\>(.*)\<\/enum\>/) {     
                # store also enum types separately
                $enums->{$parametertype_name}->{$2} = lc ($1);              
            }           
        }

        if ($parseing_structs == 1){
            $structuretype_name = lc($1) if ( /\<name\>(.*)\<\/name\>/ );
            $el_name = lc($1) if ( /"\>(.*)\<\// );
            $optional = lc($1) if ( /optional\s*=\s*"(.*?)"/ );
            $use_valid = lc($1) if ( /usevalid\s*=\s*"(.*?)"/ );
            $param_type = lc($1) if ( /param type\s*=\s*"(.*?)"/ );
            $struct_type = lc($1) if ( /struct type\s*=\s*"(.*?)"/ );
            if ( defined $param_type ) {    
                $el_name = $param_type if not defined $el_name;
                push(@{$structure_types->{$structuretype_name}{elements}},
                    {
                        el_name =>  $el_name,
                        type_name       =>  $param_type,
                        use_valid   =>  $use_valid,
                        optional    =>  $optional,
                        param   =>  undef,
                    }
                );
                $param_type = undef;
                $el_name = undef;
                $use_valid = undef;
                $optional = undef;  
            } elsif ( defined $struct_type ) {  
                $el_name = $struct_type if not defined $el_name;
                push(@{$structure_types->{$structuretype_name}{elements}},
                    {
                        el_name =>  $el_name,
                        type_name       =>  $struct_type,
                        optional   => $optional,
                        struct => undef,
                    }
                );
                $struct_type = undef;
                $el_name = undef;
            }   
        }

        if ($parseing_events == 1){
            $event_name = uc($1) if ( /\<name\>(.*)\<\/name\>/ );                     
            $el_name = lc($1) if ( /"\>(.*)\<\// );
            $optional = lc($1) if ( /optional\s*=\s*"(.*?)"/ );
            $use_valid = lc($1) if ( /usevalid\s*=\s*"(.*?)"/ );
            $seq_max_len = lc($1) if ( /seqmaxlen\s*=\s*"(.*?)"/ );
            $param_type = lc($1) if ( /param type\s*=\s*"(.*?)"/ );
            $struct_type = lc($1) if ( /struct.*type\s*=\s*"(.*?)"/ );
            # "normalize" this label name, since it comes in different versions
            $param_type =~ s/l_cause_prot_type|cause_prot_type/cause_protocol/;
            if ( /\<id\>(.*)\<\/id\>/ ){
                $id = $1;
                $events->{$id}{event_name} = $event_name;           
                
            }
            if ( defined $param_type ) {
                my $param = $parameter_types->{$param_type}; 
                $el_name = $param_type if not defined $el_name;
                push(@{$events->{$id}{elements}},
                        {
                            el_name =>  $el_name,
                            use_valid   =>  $use_valid,
                            optional    =>  $optional,
                            param       =>  $param,
                        }
                    );

                # add an entry to the print struct
                my $key = "$el_name+$param_type";
                push(@{$print_event}, { $key => undef }) unless is_existing($print_event, $key);

                $param_type = undef;
                $el_name = undef;
                $use_valid = undef;
                $optional = undef;  
            } elsif ( defined $struct_type ) {
                $el_name = $struct_type if not defined $el_name;
                # create a local array for this structure type
                my $struct = $structure_types->{$struct_type}{elements}; 
                if (defined $seq_max_len){
                    # create a new struct which contains seqmaxlen of the same struct
                    $struct = [];
                    for (my $i = 0; $i < $seq_max_len; $i++){  
                        push(@{$struct},
                            {
                                el_name => $struct_type,
                                type_name => $struct_type,
                                struct => undef,
                            }
                        );
                    }   
                }
                
                my $print_struct = [];
                &add_all_included_elements($structure_types, $parameter_types, $struct, $print_struct);
                push(@{$events->{$id}{elements}},
                        {
                            el_name => $el_name,
                            type_name => $struct_type,
                            # number of possible entries
                            seq_max_len => $seq_max_len,
                            # number of decoded entries
                            seq_len => undef,
                            # this is a ref to a struct
                            struct => $struct,
                        }
                );
                
                # add an entry to the print struct
                my $key = "$el_name+$struct_type";
                push(@{$print_event}, { $key => $print_struct }) unless is_existing($print_event, $key);
                
                $struct_type = undef;
                $el_name = undef;
                $seq_max_len = undef;
            }
        }       
    }
    close(IN);
}   

#####################################################
# Calls the decode function for respective base type
#####################################################
sub decode_base_type {
    my ($type, $type_name, $len) = @_;
    my $decoded_value;

    if (exists $decode_functions{$type}) {
        # this is the function call that will return the decoded value for this type from the event 
        $decoded_value = $decode_functions{$type}->($len, $type_name);      
    } else {
        print $OUT "type does not exist=$type\n";
    }
    
    return $decoded_value;  
}

#####################################################
# Decodes a parameter type of the event
#####################################################
sub decode_parameter_type {
    my ($elem, $cause_code_name) = @_;
    my $param = $elem->{param}; 
    my $value;
    
    #containing attribute "useValid"        
    if (defined $elem->{use_valid}) {
        my $use_valid = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, 1 ) );    
        $this_record_content_BIT_index = $this_record_content_BIT_index + 1;        
    
        # use_valid == 1 means the elem should not be printed
        $value = &decode_base_type($param->{type}, $param->{type_name}, $param->{len}) if $use_valid == 0; 
        
    #containing attribute "optional"        
    }elsif (defined $elem->{optional}) {
        my $optional = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, 1 ) );
        $this_record_content_BIT_index = $this_record_content_BIT_index + 1;        
        if( $optional == 1 )    {
            $value = "undefined"; 
        } else {
            $value = &decode_base_type($param->{type}, $param->{type_name}, $param->{len});
        }   
        
    #there is no attribute                  
    }else{
        $value = &decode_base_type($param->{type}, $param->{type_name}, $param->{len});
    }

    if ($param->{type_name} eq "bearer_cause") {
        # For CPG: The Bearer causes uses gtpv2_cause_code
        $value = $cause_code{gtpv2_cause_code}{$value}  if ( exists $cause_code{gtpv2_cause_code}{$value} );
    }
    
    if ($param->{type_name} eq "cause_code") {
        # CAUSE_CODEs are looked up using the value found in CAUSE_PROT 
        # It is assumed that the CAUSE_PROT is decoded when we come here, so that $cause_code_name has a value
        $value = $cause_code{$cause_code_name}{$value}  if ( exists $cause_code{$cause_code_name}{$value} );
    }

    if ($param->{type_name} eq "sub_cause_code") {
        # SUB_CAUSE_CODEs are looked up using the sub_cause_code enum  
        $value = $cause_code{sub_cause_code}{$value}  if ( exists $cause_code{sub_cause_code}{$value} );
    }
    
    return $value;
}   

#####################################################
# Decodes a structure type of the event
#####################################################
sub decode_structure_type {
    my ($struct, $count, $array_ref) = @_; 

    $count = @$struct unless defined $count;
    
    # for all elements in the struct
    for (my $i = 0; $i < $count; $i++) {    
        my $elem = $struct->[$i];   

        if (exists $elem->{param}) {
            my $value = &decode_parameter_type($elem);
            
            # update the print event with the decoded value
            my $key = "$elem->{el_name}+$elem->{param}->{type_name}";
            my $h_ref = &get_hash_ref($array_ref, $key);
            $h_ref->{$key} = $value if defined $value;
        } elsif (exists $elem->{struct}) {
            # lookup this struct in the print_event 
            my $key = "$elem->{el_name}+$elem->{type_name}+$i";
            my $h_ref = &get_hash_ref($array_ref, $key);

            my $process_struct = 0;
            if (defined($elem->{optional})) {
                $process_struct = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, 1 ) );
                $this_record_content_BIT_index = $this_record_content_BIT_index + 1;        
            }
            if ($process_struct == 0)
            {
                &decode_structure_type($elem->{struct}, undef, $h_ref->{$key});
            }
        } else {
            logg('error', "Could not find element in struct $elem->{el_name} $i!");
        }   
    }
}

#####################################################
# Decode one Event.
#####################################################
sub decode_event {
    my ($event) = @_;
    my $cause_code_name;
    my $value;
    
    #for all elements in the event
    my $a_ref = $event->{elements}; 
    for my $i ( 0..$#$a_ref) {  
        my $elem = $a_ref->[$i];
        if (exists $elem->{param}) {
            $value = &decode_parameter_type($elem, $cause_code_name);

            if ($elem->{param}->{type_name} eq "cause_protocol") {
                # store away this, to use it when looking up the cause code
                $cause_code_name = $value;
                
                # the cause protocol values does not match cause code names 
                if ($cause_code_name !~ /_cause$/ ) {
                    $cause_code_name .= "_cause";
                }
                $cause_code_name .= "_code";
            }

            # update the print event with the decoded value
            my $key = "$elem->{el_name}+$elem->{param}->{type_name}";
            my $hash_ref = &get_hash_ref($print_event, $key);
            $hash_ref->{$key} = $value;

            }
        elsif (exists $elem->{struct}) {
            # this block is placed here since seq_max_len is only defined on elements in events
            if (defined $elem->{seq_max_len}) { 
                $elem->{seq_len} = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, 8 ) );
                $this_record_content_BIT_index = $this_record_content_BIT_index + 8;        
                
                if ($elem->{seq_len} > $elem->{seq_max_len}) {
                    logg('error', "seq_len $elem->{seq_len} > seq_max_len $elem->{seq_max_len} in struct \$elem->{el_name}=$elem->{el_name}, \$elem->{type_name}=$elem->{type_name}!");
                }
            }
            
            # lookup this struct in the print_event 
            my $key = "$elem->{el_name}+$elem->{type_name}";
            my $hash_ref = &get_hash_ref($print_event, $key);
            my $process_struct = 0;
            if (defined($elem->{optional})) {
                $process_struct = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, 1 ) );
                $this_record_content_BIT_index = $this_record_content_BIT_index + 1;        
            }
            if ($process_struct == 0) {
                &decode_structure_type($elem->{struct}, $elem->{seq_len}, $hash_ref->{$key});
            }
        } else {
            logg('error', "Could not find element in event $elem->{el_name} $i!");
        }   
    }
}

#####################################################
# Parse content in summary, just calculate the number.
#####################################################
sub parse_content_summary {
    my $type;
    my $event_id;
    my $readp = 0;
    my $total_content_length = length ($total_content);
    my $flag = 0;
    my $event_name;

    while( $total_content_length - $readp > 0 ){
        $this_record_length = oct( "0x" . substr( $total_content, $readp, 4 ) );
        $type = oct( "0x" . substr( $total_content, $readp+4, 2 ) );

        $this_record_content = substr( $total_content, $readp, 12 );
        $readp = $readp + 2 * $this_record_length;

        if( $type == 1 ){
            $this_record_content_BIT = unpack("B48",pack("H12",$this_record_content));
            $event_id = oct( "0b" . substr( $this_record_content_BIT, 24, 8 ) );

            $flag = 1 if exists $events->{$event_id};
            if (1 == $flag){
                $events->{$event_id}{count}++;
                $flag = 0;
            }else{
                ++$nbr_unknown_event;
            }
        }
    }
}

#####################################################
# Parse the content of each event.
#####################################################
sub parse_content {
    my $print_table_header = 1;
    my $type;
    my $error_type;
    my $termination_cause;
    my $dropped_events;
    my $event_id;
    my $event;
    my $readp = 0;
    my $total_content_length = length ($total_content);
    my $first_stream_header = 1;

    while( $total_content_length - $readp > 0 )
    {
        $this_record_length = oct( "0x" . substr( $total_content, $readp, 4 ) );
        $type = oct( "0x" . substr( $total_content, $readp+4, 2 ) );
        $this_record_content = substr( $total_content, $readp, 2 * $this_record_length );
        $readp = $readp + 2 * $this_record_length;

        #$this_record_length_actual = length( $this_record_content ) / 2;
        #if( not( $this_record_length % 4 == 0 ) ){     
        #   print "$prog: Record Length for Record nr $i is not a multiple of a 32 bit word\n";
        #   exit;
        #}elsif( not( $this_record_length == $this_record_length_actual ) ){
        #   print "$prog: For Record nr $i, specified record length = $this_record_length, actual record length = $this_record_length_actual\n";
        #   exit;
        #};




        #Read header record or Stream header record  
        if( $type == 0 || $type == 4) {
            $year = oct( "0x" . substr( $this_record_content, 10, 4 ) );
            $month = oct( "0x" . substr( $this_record_content, 14, 2 ) );
            if( $month < 10 )   {
                $month = "0" . $month;
            }
            $day = oct( "0x" . substr( $this_record_content, 16, 2 ) );
            if( $day < 10 ){
                $day = "0" . $day;
            };
            $hour = oct( "0x" . substr( $this_record_content, 18, 2 ) );
            $minute = oct( "0x" . substr( $this_record_content, 20, 2 ) );
            if( $minute < 10 )  {
                $minute = "0" . $minute;
            }
            $second = oct( "0x" . substr( $this_record_content, 22, 2 ) );
            if( $second < 10 )  {
                $second = "0" . $second;
            }

        $log_ffv = oct( "0x" . substr( $this_record_content, 6, 2 ) );
        $log_fiv = oct( "0x" . substr( $this_record_content, 8, 2 ) );

        if ($version_differ eq "false") {
            if (($FFV != $log_ffv) || ($FIV != $log_fiv)) {
               &version_warning($log_ffv, $FFV, $log_fiv, $FIV)
            }
        }

            
            #Stream header record has three additional attributes! 
            if( $type == 4 ) {
                if ($first_stream_header) {
                    $first_stream_header = 0;
                    my $offset_sign = oct( "0x" . substr( $this_record_content, 24, 2 ) );
                    my $offset_hour = oct( "0x" . substr( $this_record_content, 26, 2 ) );
                    if ( $offset_hour < 10 )    {
                        $offset_hour = "0" . $offset_hour;
                    }
                    my $offset_minute = oct( "0x" . substr( $this_record_content, 28, 2 ) );
                    if ( $offset_minute < 10 )  {
                        $offset_minute = "0" . $offset_minute;
                    }
                    $utc_offset = "+";
                    $utc_offset = "-" if ($offset_sign == 1);
                    $utc_offset .= $offset_hour . ":" . $offset_minute;
                    $cause_header = oct( "0x" . substr( $this_record_content, 30, 2 ) );
                    my $length = oct( "0x" . substr( $this_record_content, 32, 2 ) );
                    $nodeid = unpack("A*",pack("H*",substr( $this_record_content, 34, 2 * $length)));
                } else {
                    # in CPG every epsc process in the node will send identical Stream Headers, only print one of those
                    $nbr_filtered_stream_headers++;
                    next;
                }
            }               
        }
        #Read record payload
        elsif( $type == 1 ) {
            $this_record_content_BIT = unpack("B*",pack("H*",$this_record_content));
            $hour = oct( "0b" . substr( $this_record_content_BIT, 34, 5 ) );
            $minute = oct( "0b" . substr( $this_record_content_BIT, 39, 6 ) );
            if( $minute < 10 )  {
                $minute = "0" . $minute;
            }
            $second = oct( "0b" . substr( $this_record_content_BIT, 45, 6 ) );
            if( $second < 10 )  {
                $second = "0" . $second;
            }
            
            my $event_id = oct( "0b" . substr( $this_record_content_BIT, 24, 8 ) );
            my $event_result = oct( "0b" . substr( $this_record_content_BIT, 32, 2 ) );
            $this_record_content_BIT_index = 24;

            if (exists $events->{$event_id}){
                $event = $events->{$event_id};# copy hash ref
                $event->{time} = "$hour:$minute:$second";
                $event->{event_id} = $event_id;
                $event->{event_result} = $event_result;
                $event->{count}++;

                if( (($decode_all eq "false") && ($event->{show} == 1)) || ($decode_all eq "true")) {
                    &decode_event($event);
                }
            }else{
                ++$nbr_unknown_event;
                print $OUT "Unsupported Event ID = $event_id\n";
                exit;
            }
        }
        #Read error record
        elsif( $type == 2 || $type == 5)    {
            $hour = oct( "0x" . substr( $this_record_content, 6, 2 ) );
            $minute = oct( "0x" . substr( $this_record_content, 8, 2 ) );
            if( $minute < 10 ) {
            $minute = "0" . $minute;
            };
            $second = oct( "0x" . substr( $this_record_content, 10, 2 ) );
            if( $second < 10 ) {
            $second = "0" . $second;
            };
            $error_type = oct( "0x" . substr( $this_record_content, 12, 2 ) );
            if ($type == 5) {
                $dropped_events = oct( "0x" . substr( $this_record_content, 14, 8 ) );
            }
        }
        #Read record footer
        elsif( $type == 3 ) {
            $termination_cause = oct( "0x" . substr( $this_record_content, 6, 2 ) );
        }
        #Unsupported Record Type
        else    {
            print $OUT "Unsupported Record Type = $type\n";
            exit;
        }

        &print_out($event, $type, $error_type, $termination_cause, $dropped_events, \$print_table_header);

        #Clear some entries for next loop. Necessary for filtering.
        if ( exists $event_record{'apn'} )  {
            delete $event_record{'apn'};
        }
        if ( exists $event_record{'tac'} )  {
            delete $event_record{'tac'};
        }
        if ( exists $event_record{'millisecond'} )  {
            delete $event_record{'millisecond'};
        }
    }
}


#####################################################
# Parse binary log files.
#####################################################
sub process_ebs_log_files {
    my @ebs_log_binaries;
    my $start_time;
    my $end_time;
    my @ebs_log_dirs=();
    my @ebs_pdirs=();
    if( defined( $ebs_file_inarg ) ){
        @ebs_log_binaries = ( $ebs_file_inarg );
    }else{ 
        (-d ${ebs_log_binary_DIR}) or die ( "Couldn't open $ebs_log_binary_DIR=$!" );
        my $ebs_files=qx(ls -1 ${ebs_log_binary_DIR}/A*eb*.* 2>/dev/null);
        chomp($ebs_files);
        if($ebs_files =~/\w+/) {
            @ebs_log_binaries=split('\n',$ebs_files);
        }
    }

    foreach $ebs_log_binary (@ebs_log_binaries){
        $total_nbr_events = 0;
        print $OUT "\n###FILE###\n";
        print $OUT "Input file=$ebs_log_binary\n";
        $start_time = localtime();

        open( EBS, $ebs_log_binary ) or die ( "Couldn't open $ebs_log_binary=$!" );
        binmode EBS;
        undef $/;
        $total_content = unpack("H*",<EBS>);
        close EBS;
        $/ = "\n";

        if( length( $total_content ) == 0 ) {
            print $OUT "$prog: Input log file is empty\n"; 
        }

        if ( $summary eq "false" ){
            &parse_content();
        }else{
            &parse_content_summary();
        }
        $end_time = localtime();
        foreach my $id (keys %{$events}){
            $total_nbr_events = $total_nbr_events + $events->{$id}{count};      
        }
        
        $total_nbr_events = $total_nbr_events + $nbr_unknown_event;
        print $OUT "###STATS###\n";
        print $OUT "Processing start time=$start_time\n";
        print $OUT "Processing end time=$end_time\n";
        foreach my $id (keys %{$events}){
            if ($events->{$id}{count} > 0) {
                print $OUT "Number of  ".$events->{$id}{event_name}."=".$events->{$id}{count}."\n";
                $events->{$id}{count} = 0;
            }
        }
        if ($nbr_filtered_stream_headers > 0) {
            print $OUT "Number of filtered stream header records=$nbr_filtered_stream_headers\n";
        }
        print $OUT "Number of unknown event=$nbr_unknown_event\n";
        print $OUT "Total number of events=$total_nbr_events\n";
    }
}

#####################################################
# Decode IE of BCD type.
#####################################################
sub bcd_coding {
    my $in = $_[0];
    my $out = "";
    while( length( $in ) > 0)
    {
        $out = $out . substr( $in, 1, 1 ) . substr( $in, 0, 1 );
        $in = substr( $in, 2 );
    }

    if( not( $out =~ m/^[fF]+$/ ) )
    {
        $out =~ s/[fF]+//;
    }

    return $out;
}

#####################################################
# Decode IE of uint type.
#####################################################
sub decode_uint {
    my ($len)=@_;
    my $uint_val = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, $len) );
    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    return $uint_val;    
}

#####################################################
# Decode IE of enum type.
#####################################################
sub decode_enum {
    my ($len, $type_name)=@_;
    my $enum_val = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, $len) );
    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;
    if (exists $enums->{$type_name}->{$enum_val}) {
        return $enums->{$type_name}->{$enum_val};
    }else {
        return $enum_val;
    }
}

#####################################################
# Decode IE of bytearray type.
#####################################################
sub decode_bytearray {
    my($len)=@_;

    my $el_len = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) );

    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    $el_len = 8 * $el_len;

    my $nr_of_bits_to_next_byte = 8 - $this_record_content_BIT_index % 8;
    if ( $nr_of_bits_to_next_byte == 8 )    {
        $nr_of_bits_to_next_byte = 0;
    }
    $this_record_content_BIT_index = $this_record_content_BIT_index + $nr_of_bits_to_next_byte;


    my $in = unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $el_len ) ) );
    (my $bytearray_val_ascii = $in) =~ s/([a-fA-F0-9]{2})/chr(hex $1)/eg;

    $this_record_content_BIT_index = $this_record_content_BIT_index + $el_len;

    return  $bytearray_val_ascii;
  }

#####################################################
# Decode IE of ipaddress type.
#####################################################
sub decode_ipaddress {
    my ($len) = @_;

    my $ipv4_val = unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) );
    $ipv4_val = hex(substr($ipv4_val,0,2)).".".hex(substr($ipv4_val,2,2)).".".hex(substr($ipv4_val,4,2)).".".hex(substr($ipv4_val,6,2));

    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    return $ipv4_val;
}

#####################################################
# Decode IE of ipaddress_v4 type.
#####################################################
sub decode_ipaddressv4 {
    my ($len) = @_;

    my $ipv4_val = unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) );
    $ipv4_val = hex(substr($ipv4_val,0,2)).".".hex(substr($ipv4_val,2,2)).".".hex(substr($ipv4_val,4,2)).".".hex(substr($ipv4_val,6,2));

    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    return $ipv4_val;
}

#####################################################
# Decode IE of ipaddress_v6 type.
#####################################################
sub decode_ipaddressv6 {
    my ($len) = @_;

    my $ipv6_val = unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) );

    $ipv6_val = (substr($ipv6_val,0,4)).":".(substr($ipv6_val,4,4)).":".(substr($ipv6_val,8,4)).":".(substr($ipv6_val,12,4)).
            ":".(substr($ipv6_val,16,4)).":".(substr($ipv6_val,20,4)).":".(substr($ipv6_val,24,4)).":".(substr($ipv6_val,28,4));
    $ipv6_val =~ s/:0{1,3}/:/g;
    $ipv6_val =~ s/^0{1,3}//g;

    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;
    return $ipv6_val;
}

#####################################################
# Decode IE of TBCD type.
#####################################################
sub decode_tbcd {
    my ($len) = @_;
    my $tbcd_val = bcd_coding( unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) ) );
    # remove pad 
    $tbcd_val =~ s/f$//g;
    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    return $tbcd_val;
}

#####################################################
# Decode IE of dnsname type.
#####################################################
sub decode_dnsname {
    my ($len) = @_;

    my $apn_length = oct( "0b" . substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) );
    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;     
    $len = 8 * $apn_length;
    #padding, moving start-of-apn to next byte
    my $nr_of_bits_to_next_byte = 8 - $this_record_content_BIT_index % 8;
    if ( $nr_of_bits_to_next_byte == 8 )
    {
        $nr_of_bits_to_next_byte = 0;
    }

    $this_record_content_BIT_index = $this_record_content_BIT_index + $nr_of_bits_to_next_byte;
    my $in = unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) );

    my $dnsname_val = "";
    my $index = 0;
    while( $index < length( $in ) )
    {
        my $length_of_one_section = 2 * oct( "0x" . substr( $in, $index, 2 ) );
        my $one_section = substr( $in, 2 + $index, $length_of_one_section );

        (my $one_section_ascii = $one_section) =~ s/([a-fA-F0-9]{2})/chr(hex $1)/eg;
        
        $dnsname_val = $dnsname_val . $one_section_ascii . ".";

        $index = $index + $length_of_one_section + 2;
    };
    
    #remove end dot
    $dnsname_val = substr( $dnsname_val, 0, length( $dnsname_val ) - 1);
    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;
    return $dnsname_val;
}

#####################################################
# Decode IE of IBCD type.
#####################################################
sub bcd_coding__3hex {
    my $in = $_[0];
    my $out = substr( $in, 2, 1 ) . substr( $in, 1, 1 ) . substr( $in, 0, 1 );
    $out =~ s/[fF]+//;  
    return $out;
}
sub decode_ibcd {
    my ($len) = @_;

    my $ibcd_val = bcd_coding__3hex( unpack("H*",pack("B*",substr( $this_record_content_BIT, $this_record_content_BIT_index, $len ) ) ) );

    $this_record_content_BIT_index = $this_record_content_BIT_index + $len;

    return $ibcd_val;
}

#####################################################
# Print out the parse result.
#####################################################
sub print_out {
    my $event = $_[0];
    my $type = $_[1];
    my $error_type = $_[2];
    my $termination_cause = $_[3];
    my $dropped_events = $_[4];
    my $print_table_header = $_[5];
    my $filter_hit;
    my $skip_next = "false";
   
    if( $type == 0 || $type == 4 ) {
        #Default format
        if( $type == 4 ) {
            print $OUT "\n###STREAM HEADER###\n";
            print $OUT "date=$year-$month-$day\n";
            print $OUT "UTC time=$hour:$minute:$second\n";
            print $OUT "UTC offset=$utc_offset\n";
            print $OUT "File format version=$log_ffv\n";
            print $OUT "File information version=$log_fiv\n"; 
            print $OUT "nodeid=$nodeid\n";
            # todo: get the string from xml
            print $OUT "cause_header=$cause_header\n\n";
        } else {
            print $OUT "\n###HEADER###\n";
            print $OUT "date=$year-$month-$day\n";
            print $OUT "time=$hour:$minute:$second\n";
            print $OUT "File format version=$log_ffv\n";
            print $OUT "File information version=$log_fiv\n\n"; 
        }
    }
    elsif( $type == 1 ) {
        my $filter_length = @filters;
        if (  $filter_length != 0 ) {
            $filter_hit = &filter($print_event);
        }else{
            $filter_hit=1;
        }

        if ($filter_hit == 1 ){
            if( (($decode_all eq "false") && ($event->{show} == 1)) || ($decode_all eq "true")) {
                if ($pr_all == 1){  
                    # Table format
                    if ($$print_table_header) {
                        &print_table_header($print_event);
                        print $OUT "\n";
                        $$print_table_header = 0;
                    }
                    &print_table_contents($print_event);
                    print $OUT "\n";
                }else{
                    if ($delimiter eq "\n") {
                        print $OUT "======EVENT======\n";
                    }
                    my $str;
                    &print_default($print_event, 0, \$str); #print out the event result
                    print $OUT "$str\n";
                    
                }
                
            }
        }
        &clear_print_event($print_event);
    }       
    elsif( $type == 2 ) {
        print $OUT "\n###ERROR###\n";
        print $OUT "occurred: $hour:$minute:$second\n";
        # todo: get the string from xml
        print $OUT "error_type=$error_type\n\n";
    }
    elsif($type == 5 ) {
        print $OUT "\n###STREAM ERROR###\n";
        print $OUT "recovered: $hour:$minute:$second\n";
        # todo: get the string from xml
        print $OUT "error_type=$error_type\n";
        print $OUT "dropped_events=$dropped_events\n\n";
    }
    elsif( $type == 3 ) {
        print $OUT "\n###FOOTER###\n";
        # todo: get the string from xml
        print $OUT "termination_cause=$termination_cause\n\n";
    };
}


#####################################################
# Print the event in default format
#####################################################
sub print_default {
    my ($event, $level, $str) = @_;
    my $param_printed = 0;

    foreach my $hash_ref (@{$event}) {
        foreach my $k (keys %$hash_ref) {
            my $value = $hash_ref->{$k};
            #extract el_name from key 
            $k =~ s/\+.*//g;
            if (ref($value) eq 'ARRAY') {
                # only print the struct name if it has defined elements
                my $sub_str;
                if (&print_default($value, $level + 1, \$sub_str) == 1) {
                    $$str .= "\t" x $level . "$k:$delimiter" . $sub_str;
                    $param_printed = 1;
                }
            } elsif (defined $value) {
                $$str .= "\t" x $level . "$k = $value$delimiter";
                $param_printed = 1;
            }
        }
    }
    return $param_printed;
}

#####################################################
# Print the event headers in table format (with -l option).
#####################################################
sub print_table_header {
    my ($event) = @_;

    foreach my $hash_ref (@{$event}) {
        foreach my $k (keys %$hash_ref) {
            my $value = $hash_ref->{$k};
            #remove type from key before printing it
            $k =~ s/\+.*//g;
            print $OUT "$k$delimiter";
            if (ref($value) eq 'ARRAY') {
                print_table_header($value);
            }
        }
    }
}

#####################################################
# Print the event content in table format (with -l option).
#####################################################
sub print_table_contents {
    my ($event) = @_;

    foreach my $hash_ref (@{$event}) {
        foreach my $k (keys %$hash_ref) {
            my $value = $hash_ref->{$k};
            if (ref($value) eq 'ARRAY') {
                print $OUT "$delimiter";
                print_table_contents($value);
            } else {
                print $OUT "$value$delimiter";
            }
        }
    }
}

#####################################################
# Erase the event content 
#####################################################
sub clear_print_event {
    my ($event) = @_;

    foreach my $hash_ref (@{$event}) {
        foreach my $k (keys %$hash_ref) {
            my $value = $hash_ref->{$k};
            if (ref($value) eq 'ARRAY') {
                clear_print_event($value);
            } else {
                # clear the value 
                $hash_ref->{$k} = undef;
            }
        }
    }
}

#####################################################
# Build filter list.
#####################################################
sub build_filter {
    my $rats;  
    my $event_ids;
    if ( $unsuccessful eq "true" ) {
        my $event_result = " ";
        foreach my $key (keys %{$enums->{event_result}}){
            my $result = $enums->{event_result}->{$key};
            $event_result .= $result." " if ($result !~ /^success$/i);
        }
        push(@filters, "event_result", "$event_result") ;
    }
    if ( $unauthenticated eq "true" ) {
        # unauthentiacted is coded as a '1'
        my $unauthenticated_text = $enums->{imsi_validation}->{1};
        push(@filters, "imsi_validation", "$unauthenticated_text") ;
    }
    $rats .= "gsm" if ( $gsm eq "true" );
    $rats .= "wcdma" if ( $wcdma eq "true" );
    push(@filters, "rat", $rats) if ( $gsm eq "true" || $wcdma eq "true" );
    push(@filters, "cause_code", ${cause_code_pattern}) if ( $cause_code eq "true" );
    push(@filters, "imsi", $imsi_pattern) if ( $imsi eq "true" );
    push(@filters, "imeisv", $tac_pattern)   if ( $tac eq "true" );
    push(@filters, "apn", $apn_pattern) if ( $apn eq "true" );
    push(@filters, "event_trigger", $trigger_pattern) if ( $trigger eq "true" );
    %filters = @filters;
}

#####################################################
# Filter the result (whether print out the event).
#####################################################
sub filter {
    my ($event) = @_;
    my $filter_hit = 1;
    my $filter_key;
    
    # go trough all filters 
    foreach $filter_key (keys %filters){ 
        
        # check if the requested filter field is included in the event 
        my $value = find_hash_ref_value($event, $filter_key);

        my $use_exact_match = 0;
        # when filtering on imsi_validation: match exact
        $use_exact_match = 1 if ($filter_key eq 'imsi_validation');
        
        # when filtering on cause_code: copy the digits only to match filter
        $value =~ s/^#(\d{2}).*/$1/ if ($filter_key eq 'cause_code');
        
        # when filtering on tac: copy the 8 first digits of imeisv to match filter
        $value = substr($value, 0, 8) if ($filter_key eq 'imeisv');

        # check if the filter matches content of the event 
        my $match = 0;
        if (defined $value) {
            if ($use_exact_match) {
                $match = $filters{$filter_key} =~ /^$value$/;
            } else {
                $match = $filters{$filter_key} =~ /\s*$value\s*/;
            }
        }
        if ($match)   {
            $filter_hit = $filter_hit & 1;
        }else {
            $filter_hit = $filter_hit & 0;
            last;
        }
    }
    return $filter_hit;
}

#####################################################
# Print out the warning.
#####################################################
sub node_warning {
    my $snode=&is_running_on_node;
    if($snode){
        #script is running on a real node. 
        print  "This script is not intended to be ran on a CPG, it might impact the capacity of the node. Are you sure you want to run the script (yes/no)? ";
        my $in=<STDIN>;
        chomp($in);
        if($in =~ /y/i){
            return 0;
        }else{
            print  "Execution is canceled by the user. Exiting...\n";
            exit 0;
        }
    }
}

#####################################################
# Print out a version mismatch warning and ask question about continue.
#####################################################
sub version_warning {
    my $got_FFV = $_[0];
    my $exp_FFV = $_[1];
    my $got_FIV = $_[2];
    my $exp_FIV = $_[3];
    print   "The Input log file has Version: $got_FFV.$got_FIV which differ from the expected Version: $exp_FFV.$exp_FIV .\n";            
    print   "If you try to decode it, you may get unpredictable results. Are you sure you want to run the script? \n";
    print   "NOTE: If the script is used for decoding several files, you will not be asked this question again. \n";
    print   "The script will continue decoding the files even if versions differ. Run script (yes/no)? ";
    my $in=<STDIN>;
    chomp($in);
    if($in =~ /y/i){
            $version_differ="true";
        return 0;
    }else{
        print  "Execution is canceled by the user. Exiting...\n";
        exit 0;
    }
}


#####################################################
# Print out a version mismatch warning.
#####################################################
sub version_warning_textonly {
    my $got_FFV = $_[0];
    my $exp_FFV = $_[1];
    my $got_FIV = $_[2];
    my $exp_FIV = $_[3];
    print   "The Input log file has Version: $got_FFV.$got_FIV which differ from the expected Version: $exp_FFV.$exp_FIV .\n";
    return 0;
}

#####################################################
# Whether it's running on node or GTT.
#####################################################
sub is_running_on_node {
    #sgsn-mme
    if (-r "/tmp/DPE_SC/LoadUnits/ttx/int/bin/load_limit.pl") {
        return 1;
    }
    #cpg-xcrp
    if (-r "/flash/eps/xml/ebm_event_specification.tar.gz") {
        return 1;
    }
    return 0;
}

#####################################################
# This subroutine checks and returns the CPU load.
#####################################################
sub working {
    my $now= time();
    if ( $sec != $now ){
        $sec=$now;
        print "$working[$wId]\b";
        $wId++;
        $wId= 0 if ( $wId >3);
    }
}
sub get_cpu_load {
    my ($nr) = @_;
    my $ret = 0;
    open(VS, "vmstat 1 $nr |") || die "Cannot run vmstat - $!";
    my (@out) = <VS>;
    close(VS);
    if (defined $out[2] && $out[2] =~ /\d+\s+/) {
        for my $i (3..($nr+1)) {
            working();
            my @arr = split(/\s+/, $out[$i]);
            if ($TTX_OS_HW =~ /sun4u/) {
                $ret += $arr[$#arr];
            } else {
                my $ver = `uname -r`;
                if ($ver =~ /\d+\.(\d+)\.(\d+)/ ) {
                    if ($1 > 5 && $2 >= 11 ){
                        $ret += $arr[$#arr-2];
                    } else {
                        $ret += $arr[$#arr-1];
                    }
                } else {
                    $ret += $arr[$#arr-1];
                }
            }
        }
    }
    $ret = 100-int($ret/($nr-1));
    return $ret;
}

#####################################################
# This subroutine checks CPU load and decides if the  script is allowed to start.
# If load caused by other processes > max_load, script pauses.
#  If load > max_load for to long, script terminates.
#####################################################
sub check_load {
    # read input parameters
    my $max_load   = shift(@_);
    my $iterations = shift(@_);
    my $flag       = shift(@_);
    my $load = 200;     # set start value greater than a 100%.
    my $i = 0;          # No of load check iterations.

    while ($load >= $max_load) {
        $load = get_cpu_load(5);
        if ($load > $max_load) {
            print ("Execution paused a few seconds due too high CPU load: $load% \n");
            sleep 3;        # Sleep until the load is lower than $max_load.
            $i++;
            if ($i > $iterations) { # After $iterations*3 sec's.
                print ("Script terminated due to persistent overload. \n");
                exit;
            }
        } else {
            if ($flag eq "initial") {
                print("CPU load check passed: load = $load% \n");
            }
            return;
        }
    }
}

sub cpu_load {
    print("Initial CPU Load Check...");
    check_load(40.0, 20, "initial"); # Max CPU load allowed
    print $OUT "\n";
  }

#####################################################
# Set Hardware and OS type, used for checking CPU load
#####################################################
sub set_ttx_env {
    my $uname=`uname -m`;
    if ($uname =~ /sun4u/) {
        $TTX_OS_HW="SunOS__sun4u";
    } elsif ($uname =~ /ppc/) {
        $TTX_OS_HW="Linux__ppcCommon";
    } else {
        $TTX_OS_HW="Linux__$uname";
    }
}

sub REAPER {
    #my $child;
    my $count=10;
    my $child=1;
    while (($child >0) && ($count > 0)) {
        $child = waitpid(-1, &WNOHANG);
        $count--;
    }
    $SIG{CHLD} = \&REAPER;  # install *after* calling waitpid
}

#####################################################
# Main
#####################################################   
#set nice
my $nice=10;
my $renice=15;
my $pid=$$;
setpriority('PRIO_PROCESS',$$,$nice) or print("Cannot set priority for process - $!\n");
set_ttx_env;
my $cpid;
my $snode=&is_running_on_node;

# Note: -r flag is at present not applicable for CPG. Flag is not listed in on-line help,
# but is still part of the code since it may become applicable later on.
getopts('hsSunlp:r:e:c:i:t::a:f:x:o:d:v:', \%opts);
if (exists $opts{h}) {
    &HELP_MESSAGE;
    exit 0;
} 
if (exists $opts{s}) {
    &print_summary;
    exit 0;
} 


&get_opts();
&parse_ebm_xml($xml_file);
parse_cause;
node_warning;
&build_filter();
redirect;

#####################################################
if ($snode) {
    cpu_load;
    &print_caution;
    `renice +$renice -p $pid`;

} else {
    process_ebs_log_files();
}
if ($$OUT !~ /STDOUT/){
    close($OUT) || die "Could not flush buffer or close output file - $!";
}
exit 0; 

