#!/bin/bash
sudo apt-get install -y build-essential
sudo apt-get install -y cmake
sudo apt-get install -y pkg-config
sudo apt-get install -y libpng12-0 libpng12-dev libpng++-dev libpng3
sudo apt-get install -y libpnglite-dev libpngwriter0-dev libpngwriter0c2
sudo apt-get install -y zlib1g-dbg zlib1g zlib1g-dev
sudo apt-get install -y libjasper-dev libjasper-runtime libjasper1
sudo apt-get install -y pngtools libtiff4-dev libtiff4 libtiffxx0c2 libtiff-tools
sudo apt-get install -y libjpeg8 libjpeg8-dev libjpeg8-dbg libjpeg-prog
sudo apt-get install -y ffmpeg libavcodec-dev libavcodec52 libavformat52 libavformat-dev
sudo apt-get install -y libgstreamer0.10-0-dbg libgstreamer0.10-0  libgstreamer0.10-dev
sudo apt-get install -y libxine1-ffmpeg  libxine-dev libxine1-bin
sudo apt-get install -y libunicap2 libunicap2-dev
sudo apt-get install -y libdc1394-22-dev libdc1394-22 libdc1394-utils
sudo apt-get install -y swig
sudo apt-get install -y libv4l-0 libv4l-dev
sudo apt-get install -y python-numpy

cd /home/hadoop/
mkdir facedata
cd facedata
hadoop fs -get s3n://info.mendlin.face/file/haarcascade_frontalface_alt.xml .
