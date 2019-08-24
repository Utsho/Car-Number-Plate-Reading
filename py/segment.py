import cv2
import numpy as np
import os
import math
import sys

hold_image=False
display_image=False
show_image_list=[]

def show_reset():
    global show_image_list
    show_image_list=[]


def showImage():
    i=0
    for img_info in show_image_list:
        if display_image:
            cv2.imshow(img_info[0],img_info[1])
    if hold_image and display_image:
        cv2.waitKey(0)
    cv2.destroyAllWindows()


p1="step1_output"
dir1=""
intrOut="step2_intermidiate"
status=open("lp_status.txt","w")




for root,d,files in os.walk(p1):
    for filename in files:
        # Join the two strings in order to form the full filepath.
        filepath = os.path.join(root, filename)
        color_image=cv2.imread(filepath)
        image=cv2.imread(filepath,0)
        rotateImage=cv2.imread("angle/"+filename,0)
        #cv2.imshow("Original", image)
        #cv2.waitKey(0)
        row = image.shape[0]
        col = image.shape[1]
        rowAngle=rotateImage.shape[0]
        colAngle=rotateImage.shape[1]
        full_area=row*col

        #print (row,col,.8*col)

        negative_image=np.zeros((row,col),np.uint8)
        
        '''total=0
        for i in range(0,row):
            for j in range(0,col):
                v=image.item(i,j)
                total+=v
        av=float(total)/float(row*col)
        
        
        
        
        for i in range(0, row):
            col_cnt = 0
            for j in range(0, col):
                v = image.item(i, j)
                if v < av:
                    col_cnt += 1
                    image.itemset(i, j, 255)
                    negative_image.itemset(i, j, 0)
                else:
                    image.itemset(i, j, 0)
                    negative_image.itemset(i, j, 255)

                    #
                    #
        image = cv2.medianBlur(image, 7)

        image = cv2.adaptiveThreshold(image, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY_INV, 31, 2)
        image = cv2.medianBlur(image, 3)'''
        blur = cv2.GaussianBlur(image, (5, 5), 0)
        ret3, th3 = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
        image=th3
        cv2.imwrite(intrOut+"/"+filename+"1.png",th3)
        cv2.imshow("th3", th3)
        cv2.waitKey(0)

        x=0
        max=0
        min=0
        for i in range(0,rowAngle):
            for j in range(0,colAngle):

                v=rotateImage.item(i, j)
                if v>max:
                    x=i
                    angle=j
                    max=v
                if v==255:
                    angle_f=float(angle)*180.0/float(colAngle)
                    print("255 found :",angle_f)
            #
        #
        cv2.rectangle(rotateImage, (angle-1, x-1), (angle +2 , x + 2), (0, 0, 255))
        cv2.imwrite("angle/"+filename,rotateImage)
        cv2.imshow('Rotate Angle',rotateImage)
        cv2.waitKey(0)

        cv2.destroyAllWindows()
        angle=float(angle)*180.0/float(colAngle)
        print("Rotation angle :",angle, max)
        if angle<70 or angle >110:
            print("rejected_for_angle")
            status.write(filename+"##"+"rejected_for_angle\n")
            continue
        
        image_center = tuple(np.array(image.shape) / 2)
        rot_mat = cv2.getRotationMatrix2D((col / 2, row / 2), 90-angle, 1)
        image = cv2.warpAffine(image, rot_mat, (col, row))
        color_image = cv2.warpAffine(color_image, rot_mat, (col, row))
        cv2.imshow("Rotated Image",image)
        cv2.imwrite(intrOut+"/"+filename+"2.png",image)
        cv2.waitKey(0)








        sz=int(.1*row)
        if sz%2==0:
            sz+=1



        '''mb=cv2.medianBlur(image,sz)


        cnt=0

        for i in range(0,row):
            for j in range(0,col):

                v= mb.item(i, j)
                if v<127:
                    cnt+=1
            #
        #

        param=float(cnt)/float(row*col)


        print("MB %",param)
        #cv2.imshow("MB", mb)
        #cv2.waitKey(0)'''





        #cv2.imshow("Accepted", image)
        #cv2.waitKey(0)
        cv2.destroyAllWindows()

        kernel = np.ones((3, 3), np.uint8)
        erosion = cv2.erode(image, kernel, iterations=1)
        neg_ero=cv2.erode(negative_image,kernel,iterations=1)
        
        kernel = np.ones((4,4), np.uint8)
        dilation = cv2.dilate(erosion, kernel, iterations=1)
        neg_dila=cv2.dilate(neg_ero, kernel, iterations=1)
        image = dilation
        cv2.imwrite(intrOut+"/"+filename+"4.png",image)







        #cv2.imwrite(intrOut+"/"+filename+"5.png",image)
        mask = np.zeros(image.shape, np.uint8)

        ret,thresh = cv2.threshold(image,127,255,0)
        contours,hierarchy = cv2.findContours(thresh, 1, 2)

        m=0

        contours, hierarchy = cv2.findContours(thresh, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
        print("middle point starts")

        cut_image_list=[]
        middlepoint_set=[]
        cout={}
        for cnt in contours:
            cv2.drawContours(mask, [cnt], 0, 255, -1)  # the [] around cnt and 3rd argument 0 mean only the particular contour is drawn

            x, y, w, h = cv2.boundingRect(cnt)
            area=w*h
            con=float(area)/float(full_area)
            newpath = ('final/' + dir1)
            if not os.path.exists(newpath):
                os.mkdir(newpath)


            if con<.15 and con>.01 and w<.6*col and h<.4*row:
                i = np.zeros((w, h), np.uint8)
                print ("b",x,y,w,h)
                cut_image_list.append((x,y,w,h))
                print (y+h/2)
                md=y+h/2
                fl=False
                for m in middlepoint_set:
                    if math.fabs(m-md)<25    :
                        cout[m]=cout[m]+1
                        fl=True
                if not fl:
                    middlepoint_set.append(md)
                    cout[md]=1

                cv2.line(color_image,(0,y+h/2),(1000,y+h/2),(0,0,255),1)
                cv2.rectangle(color_image, (x, y), (x + w, y + h), (0, 0, 255))
                #cv2.rectangle(image, (x, y), (x + w, y + h), 255)

                #m=m+1
        for i in middlepoint_set:
            print (i,cout[i])
        part_cnt=0
        final_part=[]

        print ('len',len(cut_image_list))
        for x,y,w,h in cut_image_list:
            md=y+h/2
            out=True
            for m in middlepoint_set:
                if (math.fabs(md-m)<25 and cout[m]>=2):
                    out=False
                    break
            if out:
                continue
            out=False
            for x1,y1,w1,h1 in cut_image_list:
                if x>x1 and y>y1 and x+w<x1+w1 and y+h<y1+h1:
                    print ("inside",(x,y,w,h),(x1,y1,w1,h1))
                    out=True
                    break
            if out:
                continue
            part_cnt+=1
            final_part.append((x,y,w,h))
            cv2.rectangle(color_image, (x, y), (x + w, y + h), (0, 0, 255))

        cv2.imwrite(intrOut+"/"+filename+"6.png",color_image)
        if part_cnt<7 or part_cnt>11:
	
            status.write(filename + "##" + "rejected_segment_number_not_in_range"+str(part_cnt)+"\n")
            continue



        for x,y,w,h in final_part:
            i = image[y:(y + h), x:(x + w)]
            cv2.rectangle(color_image, (x, y), (x + w, y + h), (0, 255, 0),3)

            md=y+h/2
            for m in middlepoint_set:
                if (math.fabs(md-m)<25):
                    y=m
                    break
            xx = ''
            yy = ''
            if x >= 100:
                xx = str(x)
            elif x >= 10:
                xx = '0' + str(x)
            else:
                xx = '00' + str(x)
            if y >= 100:
                yy = str(y)
            elif y >= 10:
                yy = '0' + str(y)
            else:
                yy = '00' + str(y)

            imf = newpath + "/" + filename + "_" + yy + xx + '.png'
            print(imf)
            #cv2.imshow(xx + yy, i)
            cv2.imwrite(imf, i)

        status.write(filename + "##" + "can_not_reject\n")
        print("middle point ends")
        #cv2.waitKey(0)
        cv2.destroyAllWindows()
        show_reset()
        show_image_list.append(("Final", color_image))
        cv2.imshow("Last",color_image)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

        
        showImage()
    status.close()
