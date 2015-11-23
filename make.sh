#!/bin/sh

DIR=$PWD

CLP="$DIR/ss-hmm"
cd $CLP
mvn package
cd $DIR
CP="$CLP/target/ss-hmm-1.0-jar-with-dependencies.jar"
cp $CP ss-hmm.jar

CLP="$DIR/ss-hmm-trainer"
cp ss-hmm.jar "$CLP/src/main/resources"
cd $CLP
mvn package
cd $DIR
CP="$CLP/target/ss-hmm-trainer-1.0-jar-with-dependencies.jar"
cp $CP ss-hmm-trainer.jar

CLP="$DIR/ss-hmm-predictor"
cp ss-hmm.jar "$CLP/src/main/resources"
cd $CLP
mvn package
cd $DIR
CP="$CLP/target/ss-hmm-predictor-1.0-jar-with-dependencies.jar"
cp $CP ss-hmm-predictor.jar

