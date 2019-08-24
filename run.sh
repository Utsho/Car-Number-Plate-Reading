#!/bin/bash
removeAllFile(){
    cd $1
    for i in *
    do
        rm $i
    done
    cd ..
}
cd py 
removeAllFile final
removeAllFile angle
removeAllFile step1_intermidiate
removeAllFile step1_output
removeAllFile step2_intermidiate
interpreter/virtualenvs/cv/bin/python lp_detect.py $1
python plot_radon_transform.py
python segment.py
python sc.py
python main.py

