#!/bin/bash
set -e

workdir=`pwd`

# Preinstall commons-vscode package
(cd ../commons-vscode ; npm install ; npm pack)
npm install ../commons-vscode/*-commons-vscode-*.tgz

# Copy grammar files for .properties and .yml format
curl https://raw.githubusercontent.com/textmate/yaml.tmbundle/master/Syntaxes/YAML.tmLanguage > yaml-support/yaml.tmLanguage
sed -i -e 's/string\./identifier\./g' yaml-support/yaml.tmLanguage

curl https://raw.githubusercontent.com/textmate/java.tmbundle/master/Syntaxes/JavaProperties.plist > properties-support/java-properties.tmLanguage
sed -i -e  's/constant/identifier/g' properties-support/java-properties.tmLanguage

# Clean old jars
rm -fr ${workdir}/jars
mkdir -p ${workdir}/jars

# Use maven to build fat jar of the language server
cd ${workdir}/../../headless-services/spring-boot-language-server
./build.sh
cp target/*.jar ${workdir}/jars

cd ${workdir}/../../headless-services/jdt-ls-extension
cp org.springframework.tooling.jdt.ls.extension/target/*.jar ${workdir}/jars/jdt-ls-extension.jar
cp org.springframework.tooling.jdt.ls.commons/target/*.jar ${workdir}/jars/jdt-ls-commons.jar

# Copy Reactor dependency bundles
cp org.springframework.tooling.jdt.ls.commons/target/dependencies/io.projectreactor.reactor-core.jar ${workdir}/jars/
cp org.springframework.tooling.jdt.ls.commons/target/dependencies/org.reactivestreams.reactive-streams.jar ${workdir}/jars/
