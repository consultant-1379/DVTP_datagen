FILE_REPOSITORY=$1
SESSION_START=$2
SESSION_END=$3
#echo "find -L ${FILE_REPOSITORY} -type f -name 'A*${SESSION_START}*-*${SESSION_END}*ebs*'"
RESULTS=`find -L ${FILE_REPOSITORY} -type f -name "A*${SESSION_START}*-*${SESSION_END}*ebs*"`
for RESULT in ${RESULTS}
do
	echo "${RESULT}"
done
#