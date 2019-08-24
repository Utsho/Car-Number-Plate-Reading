#!/bin/bash
sudo su
echo ""
echo "CSE324 Project"
echo "Bangladeshi car's license plate Detection"
echo ""
echo "By Al-Jamil"
echo "######  ##   ##   ##     ##   #####"
echo "##      ##   ##    ##   ##   ##   ##"
echo "######  ##   ##     ## ##    ##   ##"
echo "    ##  ##   ##      ###     ##   ##"
echo "######    ###         #       #####"
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install python2.7-dev python3.5-dev -y
sudo apt-get install oracle-java8-installer -y
sudo apt-get upgrade -y
sudo apt-get install python-opencv -y
sudo apt-get install python-pip -y
sudo apt-get install python-tk -y
sudo pip install numpy
sudo pip install scikit-image
sudo pip install tensorflow
sudo pip install prettytensor
sudo apt-get install build-essential cmake pkg-config -y
sudo apt-get install libjpeg8-dev libtiff5-dev libjasper-dev libpng12-dev -y
sudo apt-get install libavcodec-dev libavformat-dev libswscale-dev libv4l-dev -y
sudo apt-get install libxvidcore-dev libx264-dev -y
sudo apt-get install libgtk-3-dev -y
sudo apt-get install libatlas-base-dev gfortran -y
cd ~
wget -O opencv.zip https://github.com/Itseez/opencv/archive/3.1.0.zip
unzip opencv.zip
wget -O opencv_contrib.zip https://github.com/Itseez/opencv_contrib/archive/3.1.0.zip
unzip opencv_contrib.zip
sudo pip install virtualenv virtualenvwrapper
sudo rm -rf ~/.cache/pip
export WORKON_HOME=$HOME/.virtualenvs
source /usr/local/bin/virtualenvwrapper.sh
echo -e "\n# virtualenv and virtualenvwrapper" >> ~/.bashrc
echo "export WORKON_HOME=$HOME/.virtualenvs" >> ~/.bashrc
echo "source /usr/local/bin/virtualenvwrapper.sh" >> ~/.bashrc
source ~/.bashrc
mkvirtualenv cv -p python2
mkvirtualenv cv -p python3
pip install numpy
cd ~/opencv-3.1.0/
mkdir build
cd build
cmake -D CMAKE_BUILD_TYPE=RELEASE \
    -D CMAKE_INSTALL_PREFIX=/usr/local \
    -D INSTALL_PYTHON_EXAMPLES=ON \
    -D INSTALL_C_EXAMPLES=OFF \
    -D OPENCV_EXTRA_MODULES_PATH=~/opencv_contrib-3.1.0/modules \
    -D PYTHON_EXECUTABLE=~/.virtualenvs/cv/bin/python \
    -D BUILD_EXAMPLES=ON ..

make -j4
sudo make install
sudo ldconfig
cd ~/.virtualenvs/cv/lib/python2.7/site-packages/
ln -s /usr/local/lib/python2.7/site-packages/cv2.so cv2.so
cd /usr/local/lib/python3.5/site-packages/
sudo mv cv2.cpython-35m-x86_64-linux-gnu.so cv2.so
cd ~/.virtualenvs/cv/lib/python3.5/site-packages/
ln -s /usr/local/lib/python3.5/site-packages/cv2.so cv2.so
rm -R py/interpreter
cp -R ~/.virtualenvs py/interpreter
echo "###########################################################"
echo "##           ###  ######  ##  ######  #####     ############"
echo "##  ############  ######  ##  ######  ###  #####  #########"
echo "##  ############  ######  ###  ####  ###  #######  ########"
echo "##           ###  ######  ###  ####  ###  #######  ########"
echo "###########  ###  ######  ####  ##  ####  #######  ########"
echo "###########  ###  ######  ####  ##  #####  #####  #########"
echo "##           #####      ########  #########     ############"
echo "###########################################################"
echo "###########################################################"
