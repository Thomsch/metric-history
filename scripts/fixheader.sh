#!/usr/bin/env bash

if [ $# != 1 ]; then
    echo "Missing arguments. Sample: <dir>"
    exit -1
fi

DIR=$1

for filename in $DIR/*.csv; do
	sed -i 1,2d $filename
	printf "revision;class;LCOM5;NL;NLE;WMC;CBO;CBOI;NII;NOI;RFC;AD;CD;CLOC;DLOC;PDA;PUA;TCD;TCLOC;DIT;NOA;NOC;NOD;NOP;LLOC;LOC;NA;NG;NLA;NLG;NLM;NLPA;NLPM;NLS;NM;NOS;NPA;NPM;NS;TLLOC;TLOC;TNA;TNG;TNLA;TNLG;TNLM;TNLPA;TNLPM;TNLS;TNM;TNOS;TNPA;TNPM;TNS\n" > temp.csv
	cat $filename >> temp.csv
	mv temp.csv $filename
done
