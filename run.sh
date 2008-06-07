#!/bin/bash
ROOTDIR="`dirname $0`"
[ -z "$ROOTDIR" -o "$ROOTDIR" = "." ] && ROOTDIR="`pwd`"
ME="`basename $0`"
RM="/bin/rm -rf"
JAR="$ROOTDIR/bin/v7rtf.jar"
CP="$JAR"
for LIBJAR in $ROOTDIR/lib/*.jar
do
    CP="$CP:$LIBJAR"
done 
WMFMAIN="net.vitki.wmf.Test"
WMFDIR="$ROOTDIR/test/wmf"
RTFDIR="$ROOTDIR/test/rtf"
RTFMAIN="net.vitki.rtf.DocAnalyser"
STYLEMAIN="net.vitki.rtf.StyleRetriever"
RTFOPT="$RTFDIR/options.opt"
case "$1" in
build)
	ant
	;;
wmftest)
	echo "==== WMF test ===="
	cd $WMFDIR
	java -cp $CP $WMFMAIN all $WMFDIR
	cd $ROOTDIR
	;;
wmfuntest)
	cd $WMFDIR
	$RM *.png *.jpg *.log
	cd $ROOTDIR
	;;
rtftest)
	echo "==== RTF test ===="
	cd $RTFDIR
	for FILE in *.rtf
	do
	  NAME=${FILE/%.rtf/}
	  echo "==== $NAME ... ===="
	  java -cp $CP $RTFMAIN $RTFDIR/$NAME $RTFOPT
	  ZIP="$RTFDIR/$NAME.zip"
	  ZIPDIR="$RTFDIR/$NAME.dir"
	  [ -r $ZIP ] || continue
	  mkdir $ZIPDIR 2> /dev/null
	  (cd $ZIPDIR && jar xf $ZIP)
	done
	cd $ROOTDIR
	;;
rtfuntest)
	cd $RTFDIR
	$RM *.dir *.xml *.txt *.log *.zip
	cd $ROOTDIR
	;;
test)
	$0 wmftest
	$0 rtftest
	;;
untest)
	$0 wmfuntest
	$0 rtfuntest
	;;
clean)
	ant clean
	$0 untest
	;;
*)
	echo "usage: $ME build|clean|test|untest|wmftest|wmfuntest|rtftest|rtfuntest"
	exit 1
esac
