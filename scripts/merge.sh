#!/usr/bin/env bash

if [ $# != 2 ]; then
	echo "Merges CSV files in <DIR> to file <OUTPUT>"
    echo "Missing arguments. Sample: <dir> <output>"
    exit -1
fi

# Replaces backslashes with forward slashes
DIR=${1//\\//}
OUTPUT=${2//\\//}

DIR=${DIR%/}

echo "revision;class;LCOM5;NL;NLE;WMC;CBO;CBOI;NII;NOI;RFC;AD;CD;CLOC;DLOC;PDA;PUA;TCD;TCLOC;DIT;NOA;NOC;NOD;NOP;LLOC;LOC;NA;NG;NLA;NLG;NLM;NLPA;NLPM;NLS;NM;NOS;NPA;NPM;NS;TLLOC;TLOC;TNA;TNG;TNLA;TNLG;TNLM;TNLPA;TNLPM;TNLS;TNM;TNOS;TNPA;TNPM;TNS" > $OUTPUT
tail -q -n +2 $DIR/*.csv >> $OUTPUT

exit 0
