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
cd ..

