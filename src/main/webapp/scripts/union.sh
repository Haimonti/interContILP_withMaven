#!/bin/sh

# Go to the directory where yap is been installed
cd ../../../../../../yap-6.2.2/
# Execute the union script
# Assume that features_100b.pl is the file that was uploaded and resides in /uploadFiles
# directory of the webapp. The output file is the required union - features_100ab.pl
./union_features_v1.sh features_100a.pl ../tomcat/apache-tomcat-9.0.16/webapps/interContILP/uploadFiles/features_100b.pl features_100ab.pl
# Generate the arff file. This relies on the fact that the
# server has a version of classes_100a.pl corresponding to features_100a.pl
# This still needs to happen in the yap directory
./portray_features.sh classes_100a.pl features_100ab.pl
# Move the arff files to the uploadFiles directory of the webapp
mv features_100ab.pl ../tomcat/apache-tomcat-9.0.16/webapps/interContILP/uploadFiles/
# Now execute the consensus protocol


