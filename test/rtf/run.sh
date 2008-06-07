#!/bin/bash
dir="`pwd`"
jar="$dir/../bin/v7rtf.jar:$dir/../lib/xalan-2.3.1-ru.jar"
opts="$dir/options.opt"
main="ru.vihens.rtf2xml.StyleRetriever"
main="ru.vihens.rtf2xml.DocAnalyser"
for file in *.rtf
do
  name=${file/%.rtf/}
  echo "==== $name ... ===="
  java -cp $jar $main $dir/$name $opts
  zip="$dir/$name.zip"
  zdir="$dir/$name.dir"
  if [ -r $dir/$name.zip ]
  then
    mkdir $zdir 2> /dev/null
    if [ -d $zdir ]
    then
      cd $zdir
      jar xf $zip
      cd $dir
    fi
  fi
done
