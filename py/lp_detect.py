import cv2
# Importing the Opencv Library
import numpy as np
import sys
import os
from math import *
# Importing NumPy,which is the fundamental package for scientific computing with Pythond
rejected_for_color=0

def color_analysis(image):
    global rejected_for_color
    r=image.shape[0]
    c=image.shape[1]

    w=0
    b=0
    g=0
    o=0
    im = np.zeros((r, c,3), np.uint8)
    for i in range (0,r):
        for j in range(0,c):
            if image.item(i,j,0) > 127 :
                #image.itemset(i,j,0,255)
                im.itemset(i,j,0,255)
            else :
                #image.itemset(i,j,0,0)
                im.itemset(i, j, 0, 0)
            if image.item(i,j,1) > 127 :
                #image.itemset(i,j,1,255)
                im.itemset(i, j, 1, 255)
            else :
                #image.itemset(i,j,1,0)
                im.itemset(i, j, 1, 0)
            if image.item(i,j,2) > 127 :
                #image.itemset(i,j,2,255)
                im.itemset(i, j, 2, 255)

            else :
                #image.itemset(i,j,2,0)
                im.itemset(i, j, 2, 0)
            if im.item(i,j,0)==255 and im.item(i,j,1)==0 and im.item(i,j,2)==0:
                im.itemset(i, j, 0, 255)
                im.itemset(i, j, 1, 255)
                im.itemset(i, j, 2, 255)
            if im.item(i,j,0)==255 and im.item(i,j,2)==255 and im.item(i,j,1)==255:
                w+=1
            elif im.item(i, j, 0) == 0 and im.item(i, j, 2) == 0 and im.item(i, j, 1) == 0:
                b += 1
            elif im.item(i, j, 0) == 0 and im.item(i, j, 2) == 0 and im.item(i, j, 1) == 255:
                g += 1
            else:
                o+=1
    w=float(w)/float(r*c)
    g = float(g) / float(r * c)
    b = float(b) / float(r * c)
    o = float(o) / float(r * c)
    #print(w,b,g,o)
    if o>.1:
        rejected_for_color+=1
        return False
    else:
        return True
    wh=0
    bl=0
    cv2.medianBlur(im,3)

    for i in range (0,r):
        for j in range(0,c):
            gray=.72*im.item(i,j,2)+.21*im.item(i,j,0)+.07*im.item(i,j,1)
            if gray>127:
                wh+=1
            else:
                bl+=1
    pr_wh=float(wh)/float(r*c)
    pr_bl=float(bl)/float(r*c)
    if pr_wh==0 or pr_bl==0:
        return (False,0,-1)
    entropy=-pr_wh*log2(pr_wh)-pr_bl*log2(pr_bl)
    #print("enrtopy:",entropy)
    if entropy<.3:
        return (False,0,-1)

    return (True,image,entropy)






intrOut="step1_intermidiate"
if True:

            filename='step1_output'
            filepath=sys.argv[1]



            # Join the two strings in order to form the full filepath.
            # Reading Image
            img1=cv2.imread(filepath)
            #cv2.imshow('ori',img1)
            #cv2.waitKey(0)
            img = cv2.imread(filepath)
            row = img.shape[0]
            col = img.shape[1]
            div=sqrt(row*col)
            #print(row,col)
            div=3222.322144044571/div
            img1=cv2.resize(img1,(int(col*div),int(row*div)))
            img = cv2.resize(img, (int(col * div), int(row * div)))
            row = img.shape[0]
            col = img.shape[1]
            #print(row,col)
            show=cv2.resize(img,(int(col/5),int(row/5)))
            #cv2.namedWindow("1-Original Image",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            #cv2.imshow("1-Original Image",show)

            #cv2.waitKey(0)
            # Display image

            # RGB to Gray scale conversion

            img_gray = cv2.cvtColor(img,cv2.COLOR_RGB2GRAY)
            
            #cv2.namedWindow("2-Gray Converted Image",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(img_gray, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/1.png",show)
            #cv2.imshow("2-Gray Converted Image",show)
            #cv2.waitKey(0)
            # Display Image

            # Noise removal with iterative bilateral filter(removes noise while preserving edges)
            noise_removal = cv2.bilateralFilter(img_gray,9,75,75)
            #cv2.namedWindow("3-Noise Removed Image",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(noise_removal, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/2.png",show)
            #cv2.imshow("3-Noise Removed Image",show)
            #cv2.waitKey(0)
            # Display Image

            # Histogram equalisation for better results
            equal_histogram = cv2.equalizeHist(noise_removal)
            #cv2.namedWindow("4-After Histogram equalisation",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(img_gray, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/3.png",show)

            #cv2.imshow("4-After Histogram equalisation",show)
            #cv2.waitKey(0)
            # Display Image

            # Morphological opening with a rectangular structure element
            kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(9,9))
            morph_image = cv2.morphologyEx(equal_histogram,cv2.MORPH_OPEN,kernel,iterations=15)
            #cv2.namedWindow("5-Morphological opening",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(morph_image, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/4.png",show)

            #cv2.imshow("5-Morphological opening",show)
            #cv2.waitKey(0)
            # Display Image

            # Image subtraction(Subtracting the Morphed image from the histogram equalised Image)
            sub_morp_image = cv2.subtract(equal_histogram,morph_image)
            #cv2.namedWindow("6-Subtraction image", cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(sub_morp_image, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/5.png",show)

            #cv2.imshow("6-Subtraction image", show)
            #cv2.waitKey(0)
            # Display Image

            # Thresholding the image
            ret,thresh_image = cv2.threshold(sub_morp_image,0,255,cv2.THRESH_OTSU)
            #cv2.namedWindow("7-Image after Thresholding",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(thresh_image, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/6.png",show)

            #cv2.imshow("7-Image after Thresholding",show)
            #cv2.waitKey(0)
            # Display Image

            # Applying Canny Edge detection
            canny_image = cv2.Canny(thresh_image,250,255)
            #cv2.namedWindow("8-Image after applying Canny",cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(canny_image, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/7.png",show)

            #cv2.imshow("8-Image after applying Canny",show)
            #cv2.waitKey(0)
            # Display Image
            canny_image = cv2.convertScaleAbs(canny_image)

            # dilation to strengthen the edges
            kernel = np.ones((7,7), np.uint8)
            # Creating the kernel for dilation
            dilated_image = cv2.dilate(canny_image,kernel,iterations=1)
            #cv2.namedWindow("9-Dilation", cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(dilated_image, (int(col/5), int(row/5)))
            cv2.imwrite(intrOut+"/8.png",show)

            #cv2.imshow("9-Dilation", show)

            kernel = np.ones((3, 3), np.uint8)
            # Creating the kernel for dilation
            dilated_image = cv2.erode(dilated_image, kernel, iterations=1)
            # cv2.namedWindow("9-Dilation", cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(dilated_image, (int(col / 5), int(row / 5)))

            #cv2.imshow("9-Dilation", show)

            kernel = np.ones((9 , 9), np.uint8)
            # Creating the kernel for dilation
            dilated_image = cv2.dilate(dilated_image, kernel, iterations=1)
            # cv2.namedWindow("9-Dilation", cv2.WINDOW_NORMAL)
            # Creating a Named window to display image
            show = cv2.resize(dilated_image, (int(col / 5), int(row / 5)))

            #cv2.imshow("9-Dilation", show)
            #cv2.waitKey(0)
            # Displaying Image

            # Finding Contours in the image based on edges
            new,contours, hierarchy = cv2.findContours(dilated_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
            contours= sorted(contours, key = cv2.contourArea, reverse = True)[:10]
            # Sort the contours based on area ,so that the number plate will be in top 10 contours
            screenCnt = None
            # loop over our contours
            found=0
            list_found=[]
            list_image=[]
            en_list=[]
            for c in contours:
                # approximate the contour

                x, y, w, h = cv2.boundingRect(c)

                peri = cv2.arcLength(c, True)



                approx = cv2.approxPolyDP(c, 0.06 * peri, True)  # Approximating with 6% error
                # if our approximated contour has four points, then
                # we can assume that we have found our screen
                #print(len(approx ))

                cv2.rectangle(img1, (x, y), (x + w, y + h), (0, 0, 255), 10)
                show = cv2.resize(img, (int(col / 5), int(row / 5)))
                #print(w,h)
                cv2.imshow("Box", show)
                #cv2.waitKey(0)
                #cv2.destroyAllWindows()
                if h>=150 and h<750 and w>150 and w<1600:
                        found+=1
                        screenCnt = approx
                        cv2.rectangle(img1, (x, y), (x + w, y + h), (0, 255, 0), 10)
                        flag = False

                        for prev in list_found:
                            px=prev[0]
                            py=prev[1]
                            pw=prev[2]
                            ph=prev[3]
                            if px<x and px+pw>x and pw>w and py<y and py+ph>y and ph>h:
                                flag=True
                                break
                            elif x<px and x+w>px and w>pw and y<py and y+h>py and h>ph:
                                index=list_found.index(prev)
                                list_found.remove(prev)
                                list_image=list_image[:index]+list_image[index+1:]
                                en_list=en_list[:index]+en_list[index+1:]
                                break

                        i1 = img[y:y + h, x:x + w]
                        f= color_analysis(i1)
                        #if not f:
                         #   continue

                        i1=cv2.cvtColor(i1,cv2.COLOR_RGB2GRAY)
                        #mask=np.zeros(i1.shape,np.uint8)
                        #i1=cv2.bitwise_and(i1,i1,mask=mask)

                        list_found.append((x,y,w,h))
                       # cv2.imshow("pae",i)
                        list_image.append(i1)
                        #en_list.append(en)
                       # cv2.waitKey(0)
                       # cv2.destroyAllWindows()

           #print(len(list_found))
            show = cv2.resize(img1, (int(col / 5), int(row / 5)))
            cv2.imwrite(intrOut+"/9.png",show)
            #cv2.imshow("Box",show)
            # cv2.waitKey(0)
            #cv2.destroyAllWindows()
            cnt=0
            for img in list_image:
                #equ = cv2.equalizeHist(img)
                #res = np.hstack((img,equ)) #stacking images side-by-side
                outputFile=filename
                if not os.path.exists(outputFile):
                    os.mkdir(outputFile)
                cv2.imwrite(outputFile+"/"+str(cnt)+"_"+".png",img)
                print(outputFile+"/"+str(cnt)+"_"+".png")
                cnt+=1




