# Demo Project


Requirements
1. Graalvm and native installed [click here for info](https://docs.oracle.com/en/learn/graalvm-native-image-quick-start/index.html#introduction)

2. Docker desktop with kubernetes enabled [click here more info ](https://www.docker.com/products/docker-desktop/)

---

The aim of this project is to get a kafka cluster with a 

Spring boot native sender to a topic

have three consumers

1. Spring boot JVM,
2. Spring boot native
3. Golang

consume the messages and compare the performance. This is done in a kubernetes env

---

Getting started

The commands have been bundled up for conenviece and reduce errors

---
First load the commands into a bash shell 

        source ./tools/tools.sh
---
Second setup the kafka broker

    setupKube

This will setup metrics server, the kafka cluster and add in two Topics

* test-input
* error-output

This might take some time

---

Third 

Build the images

    buildAllDockerImages
    
This will take time, native build does take more time than usual.

---
Third

Setup Observations

in two other shells execute

    Watch the metrics
    source ./tools/tools.sh
    watchTop 

In another shell
 
    watch the pods spin up
    source ./tools/tools.sh
    watchPo

---

Fourth 
    
Run the application sender first

    applySender

Then the consumers

    applyConsumers

Watch the previous shells for metrics and observatios, 
You can do a 

    kubectl logs <po>

    remember to execute this command for auto-complete 
    
    source <(kubectl completion bash)

---

Fifth 

remove everything

    deleteSender
    deleteConsumers
    tearDownKube