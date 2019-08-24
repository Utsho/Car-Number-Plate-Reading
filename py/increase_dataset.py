import cv2
import numpy as np
import random
import sys

import os

src='datasetOriginal'
out='dataset'
for root,directories,fs in os.walk(src):
    dirlen=len(directories)
    i=0
    for dirname in directories:
        print('>>>> Currently Processing Dir:',dirname,'[',i,'/',dirlen,']')
        dirpath = os.path.join(root, dirname)
        outdirpath=os.path.join(out,dirname)
        if not os.path.exists(outdirpath):
            os.makedirs(outdirpath)
        for r1,d,files in os.walk(dirpath):
            flen=len(files)
            j=0
            for filename in files:
                print('..', '[', i, '/', dirlen, ']',filename,'{',j,'/',flen,'}')
                filepath=os.path.join(r1,filename)
                image=cv2.imread(filepath)
                row=image.shape[0]
                col=image.shape[1]

                outfile = filename
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, image)

                ang=random.randint(1,5)
                M=cv2.getRotationMatrix2D((col/2,row/2),ang,1)
                dst=cv2.warpAffine(image,M,(col,row))
                outfile=filename+'1.png'
                outfilepath=os.path.join(outdirpath,outfile)
                cv2.imwrite(outfilepath,dst)


                ang = -random.randint(1, 5)
                M = cv2.getRotationMatrix2D((col / 2, row / 2), ang, 1)
                dst = cv2.warpAffine(image, M, (col, row))
                outfile = filename + '2.png'
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, dst)

                dx=random.randint(10,25)
                dy=0
                dst=cv2.resize(image,(col+dy,row+dx))
                outfile = filename + '3.png'
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, dst)

                dx = -random.randint(10, 25)
                dy = 0
                dst = cv2.resize(image, (col + dy, row + dx))
                outfile = filename + '4.png'
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, dst)

                dy = random.randint(10, 25)
                dx = 0
                dst = cv2.resize(image, (col + dy, row + dx))
                outfile = filename + '5.png'
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, dst)


                dy = -random.randint(10, 25)
                dx = 0
                dst = cv2.resize(image, (col + dy, row + dx))
                outfile = filename + '6.png'
                outfilepath = os.path.join(outdirpath, outfile)
                cv2.imwrite(outfilepath, dst)

                j=j+1
            #
        #
        i=i+1
    #
#



