#!/bin/sh

# create results folder
rslt="/home/hduser/results"
hd_home="/home/hduser"

# Input Filename
filename="cho_norm.txt"

# Number of Clusters
k=5

# Max Iterations
maxIter=10


base="/SkyDrive/svn/CSE601/DataMining2"

printf "FILENAME=$filename\nNUMCLUSTERS=$k\nMAXITER=$maxIter" > $base/KMeansMR.properties
#printf "FILENAME=$filename\nNUMCLUSTERS=$k\nMAXITER=$maxIter\nCENTROIDSFILE=/SkyDrive/svn/CSE601/DataMining2/initialCentroids.txt" > $base/KMeansMR.properties



echo "Creating results dir @" "$rslt"
mkdir -m a=rwx $rslt

# Create jar

cd $base
sudo jar cvfM $base/Dist/gene_KMeans.jar * META-INF/* src/* -C bin *

cd $hd_home

# copy jar to holding area
cp $base/Dist/gene_KMeans.jar .
echo "copying jar file to" "$hd_home"

# copy input to holding area
mkdir -m a=rwx $hd_home/Input
cp $base/Input/$filename $hd_home/Input
echo "copying input file to" "$hd_home/Input"

# copy input files into hdfs
hdfs dfs -rm /input/*
hdfs dfs -copyFromLocal $base/Input/$filename /input

# copy configuration files into hdfs
hdfs dfs -rm /conf/*
hdfs dfs -copyFromLocal $base/conf/configuration.properties /conf

# create and copy properties file to holding area
cp $base/KMeansMR.properties .
echo "copying properties file to" "$hd_home"

echo $rslt/$k/*
sudo rm -r $rslt/$k/*

echo $base/Results/$k/*
sudo rm -r $base/Results/$k/*

# run the jar for different values of k
hadoop jar gene_KMeans.jar edu.buffalo.cse601.datamining.project2.kmeansMR.KMeansDriver $k

# copy reducer output to results
hdfs dfs -copyToLocal /output/part* $rslt
hdfs dfs -copyToLocal /conf/centroids $rslt

#copy output to local folder
echo "moving to the results folders"
cd "$rslt"
echo $PWD
mkdir -m a=rwx "$k"
mv part* "$k"
mv centroids "$k"

# going back to hd_user
echo "moving to home hduser home" $hd_home
cd "$hd_home"
echo $PWD

sudo cp -r $rslt/$k/* $base/Results/$k
#sudo chown -R hash $base/Results
#cd $base
#java -classpath "bin:lib/*" edu.buffalo.cse601.datamining.project2.kmeansMR.PlotClustersPCA
