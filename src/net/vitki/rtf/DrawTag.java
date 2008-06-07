package net.vitki.rtf;

import java.util.Hashtable;

public abstract class DrawTag implements DrawConst
{
    public static final int T_NONE   = 0;
    public static final int T_VALUE  = 1;
    public static final int T_BOOL   = 2;
    public static final int T_STRING = 3;
    public static final int T_ARRAY  = 4;
    public static final int T_EMU    = 5;
    public static final int T_COLOR  = 6;
    public static final int T_TWIPS  = 7;
    public static final int T_FIXED  = 8;
    public static final int T_INT    = 9;
    public static final int T_LONG   = 10;
    public static final int T_ANGLE  = 11;
    public static final int T_BINARY = 12;

    public static int getNo (String name)
    {
        Integer val = (Integer)name2no.get(name);
        return val == null ? 0 : val.intValue();
    }

    public static String getName (int no)
    {
        return no2name[no];
    }

    public static int getType (int no)
    {
        return no2type[no];
    }
    
    public static String getDefault (int no)
    {
        return no2def[no];
    }

    public static boolean getSkip (int no)
    {
        return no2skip[no];
    }

    private static final int MAX = 350;

    private static int[]      no2type = null;
    private static boolean[]  no2skip = null;
    private static String[]   no2name = null;
    private static String[]   no2def  = null;
    private static Hashtable  name2no = null;

    private static synchronized void setupTags()
    {
        if (name2no != null)
            return;
        no2type = new int[MAX];
        no2name = new String[MAX];
        no2skip = new boolean[MAX];
        no2def = new String[MAX];
        name2no = new Hashtable();
        defaultTags();
        no2skip[0] = true;
    }

    private static void add (String name, int no, int type, String def, boolean allowed)
    {
        no2type[no] = type;
        no2name[no] = name;
        no2def[no] = def;
        no2skip[no] = !allowed;
        name2no.put( name, new Integer(no) );
    }

    private static void defaultTags()
    {
        add(  "posh"  , _posh , T_VALUE, null, true  ); // [...]   Horizontal alignment (HALIGN_*)
        add(  "posrelh" , _posrelh  , T_VALUE, null, true  ); // [...]   Position horizontally relative to (HPOS_*)
        add(  "posv"  , _posv , T_VALUE, null, true  ); // [...]   Vertical alignment (VALIGN_*)
        add(  "posrelv" , _posrelv  , T_VALUE, null, true  ); // [...]   Position vertically relative to (VPOS_*)
        add(  "fLayoutInCell" , _fLayoutInCell  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "fAllowOverlap" , _fAllowOverlap  , T_BOOL, "1", false  ); // [TRUE]   ...
        add(  "fChangePage" , _fChangePage  , T_BOOL, "0", false  ); // [FALSE]   Anchor may change page.
        add(  "fIsBullet" , _fIsBullet  , T_BOOL, "0", false  ); // [FALSE]   Whether picture was inserted as a bullet.
        add(  "rotation"  , _Rotation , T_ANGLE, "0", true ); // [0]   Rotation of the shape.
        add(  "fFlipV"  , _fFlipV , T_BOOL, "0", false  ); // [FALSE]   Vertical flip, applied after the rotation.
        add(  "fFlipH"  , _fFlipH , T_BOOL, "0", false  ); // [FALSE]   Horizontal flip, applied after the rotation.
        add(  "shapeType" , _ShapeType  , T_VALUE, "0", true  ); // [None]    as is.
        add(  "wzName"  , _wzName , T_STRING, null, false  ); // [NULL]    Shape name.
        add(  "pWrapPolygonVertices"  , _pWrapPolygonVertices , T_ARRAY, null, false ); // [NULL]   Points of the text wrap polygon.
        add(  "dxWrapDistLeft"  , _dxWrapDistLeft , T_EMU, "114305", false ); // [114,305]   Left wrapping distance from text.
        add(  "dyWrapDistTop" , _dyWrapDistTop  , T_EMU, "0", false ); // [0]   Top wrapping distance from text.
        add(  "dxWrapDistRight" , _dxWrapDistRight  , T_EMU, "114305", false ); // [114,305]   Right wrapping distance from text.
        add(  "dyWrapDistBottom"  , _dyWrapDistBottom , T_EMU, "0", false ); // [0]   Bottom wrapping distance from text.
        add(  "fBehindDocument" , _fBehindDocument  , T_BOOL, "0", false  ); // [FALSE]   Place the shape behind text.
        add(  "fIsButton" , _fIsButton  , T_BOOL, "0", false  ); // [FALSE]   clicking performs an action.
        add(  "fHidden" , _fHidden  , T_BOOL, "0", false  ); // [FALSE]   Do not display or print.
        add(  "pihlShape" , _pihlShape  , T_STRING, null, false  ); // [NULL]    The hyperlink in the shape.
        add(  "fArrowheadsOK" , _fArrowheadsOK  , T_BOOL, "0", false  ); // [FALSE]   Allow arrowheads.
        add(  "fBackground" , _fBackground  , T_BOOL, "0", false  ); // [FALSE]   This is the background shape.
        add(  "fDeleteAttachedObject" , _fDeleteAttachedObject  , T_BOOL, "0", false ); // [FALSE]   Delete object attached to shape.
        add(  "fEditedWrap" , _fEditedWrap  , T_BOOL, "0", false  ); // [FALSE]   Wrap polygon was edited.
        add(  "fHitTestFill"  , _fHitTestFill , T_BOOL, "1", false  ); // [TRUE]    Hit test fill.
        add(  "fHitTestLine"  , _fHitTestLine , T_BOOL, "1", false  ); // [TRUE]    Hit test lines.
        add(  "fInitiator"  , _fInitiator , T_BOOL, null, false  ); // [NULL]    Set by the solver.
        add(  "fNoFillHitTest"  , _fNoFillHitTest , T_BOOL, "0", false  ); // [FALSE]   Hit test a shape as though filled.
        add(  "fNoHitTestPicture" , _fNoHitTestPicture  , T_BOOL, "0", false  ); // [FALSE]   Do not hit test the picture.
        add(  "fNoLineDrawDash" , _fNoLineDrawDash  , T_BOOL, "0", false  ); // [FALSE]   Draw a dashed line if no line exists.
        add(  "fOleIcon"  , _fOleIcon , T_BOOL, "0", false  ); // [FALSE]   For OLE objects..
        add(  "fOnDblClickNotify" , _fOnDblClickNotify  , T_BOOL, "0", false  ); // [FALSE]   Notify client on a double click.
        add(  "fOneD" , _fOneD  , T_BOOL, "0", false  ); // [FALSE]   1D adjustment.
        add(  "fPreferRelativeResize" , _fPreferRelativeResize  , T_BOOL, "0", false ); // [FALSE]   For UI only. Prefer relative resizing.
        add(  "fPrint"  , _fPrint , T_BOOL, "1", false  ); // [TRUE]    Print this shape.
        add(  "hspMaster" , _hspMaster  , T_STRING, null, false  ); // [NULL]    Master shape.
        add(  "hspNext" , _hspNext  , T_STRING, null, false  ); // [NULL]    ID of the next shape for linked text.
        add(  "xLimo" , _xLimo  , T_LONG, null, false  ); // [None]    Defines the limo stretch point.
        add(  "yLimo" , _yLimo  , T_LONG, null, false  ); // [None]    Defines the limo stretch point.
        add(  "fLockRotation" , _fLockRotation  , T_BOOL, "0", false  ); // [FALSE]   Lock rotation.
        add(  "fLockAspectRatio"  , _fLockAspectRatio , T_BOOL, "0", false  ); // [FALSE]   Lock aspect ratio.
        add(  "fLockAgainstSelect"  , _fLockAgainstSelect , T_BOOL, "0", false  ); // [FALSE]   Lock against selection.
        add(  "fLockCropping" , _fLockCropping  , T_BOOL, "0", false  ); // [FALSE]   Lock against cropping.
        add(  "fLockVerticies"  , _fLockVerticies , T_BOOL, "0", false  ); // [FALSE]   Lock against edit mode.
        add(  "fLockText" , _fLockText  , T_BOOL, "0", false  ); // [FALSE]   Lock text against editing.
        add(  "fLockAdjustHandles"  , _fLockAdjustHandles , T_BOOL, "0", false  ); // [FALSE]   Lock adjust handles.
        add(  "fLockAgainstGrouping"  , _fLockAgainstGrouping , T_BOOL, "0", false  ); // [FALSE]   Lock against grouping.
        add(  "fLockShapeType"  , _fLockShapeType , T_BOOL, "0", false  ); // [FALSE]   Lock the shape type.
        add(  "dxTextLeft"  , _dxTextLeft , T_EMU, "91440", false ); // [91,440]    Left internal margin of the text box.
        add(  "dyTextTop" , _dyTextTop  , T_EMU, "45720", false ); // [45,720]    Top internal margin of the text box.
        add(  "dxTextRight" , _dxTextRight  , T_EMU, "91440", false ); // [91,440]    Right internal margin of the text box.
        add(  "dyTextBottom"  , _dyTextBottom , T_EMU, "45720", false ); // [45,720]    Bottom internal margin of the text box.
        add(  "WrapText"  , _WrapText , T_VALUE, "0", false  ); // [0]   Wrap text at margins (WRAP_*)
        add(  "anchorText"  , _anchorText , T_VALUE, "0", false  ); // [0]   Text anchor point (TEXT_ANCHOR_*)
        add(  "txflTextFlow"  , _txflTextFlow , T_VALUE, "0", true  ); // [0]   Text flow (TEXT_FLOW_*)
        add(  "cdirFont"  , _cdirFont , T_VALUE, "0", true  ); // [0]   Font rotation (FONT_ROT_*)
        add(  "fAutoTextMargin" , _fAutoTextMargin  , T_BOOL, "0", false  ); // [FALSE]   Use host's margin calculations.
        add(  "scaleText" , _scaleText  , T_LONG, "0", false  ); // [0]   Text zoom and scale.
        add(  "lTxid" , _lTxid  , T_LONG, "0", false  ); // [0]   ID for the text.
        add(  "fRotateText" , _fRotateText  , T_BOOL, "0", false  ); // [FALSE]   Rotate text with shape.
        add(  "fSelectText" , _fSelectText  , T_BOOL, "1", false  ); // [TRUE]    TRUE if single click selects text
        add(  "fFitShapeToText" , _fFitShapeToText  , T_BOOL, "0", false  ); // [FALSE]   Adjust shape to fit text size.
        add(  "fFitTextToShape" , _fFitTextToShape  , T_BOOL, "0", false  ); // [FALSE]   Adjust text to fit shape size.
        add(  "gtextUNICODE"  , _gtextUNICODE , T_STRING, null, true  ); // [NULL]   Unicode text string.
        add(  "gtextAlign"  , _gtextAlign , T_VALUE, "1", false  ); // [1]   Alignment on curve: (ALIGN_CURVE_*)
        add(  "gtextSize" , _gtextSize  , T_FIXED, "2359296", true ); // [2,359,296]   Default point size.
        add(  "gtextSpacing"  , _gtextSpacing , T_FIXED, "65536", false ); // [65,536]   ...
        add(  "gtextFont" , _gtextFont  , T_STRING, null, true  ); // [NULL]    Font name.
        add(  "fGtext"  , _fGtext , T_BOOL, "0", false  ); // [FALSE]   True if the (gtext*) are used.
        add(  "gtextFVertical"  , _gtextFVertical , T_BOOL, "0", false  ); // [FALSE]   If available, an @ font should be used.
        add(  "gtextFKern"  , _gtextFKern , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "gtextFTight" , _gtextFTight  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "gtextFStretch" , _gtextFStretch  , T_BOOL, "0", false  ); // [FALSE]   Stretch the text to fit the shape.
        add(  "gtextFShrinkFit" , _gtextFShrinkFit  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "gtextFBestFit" , _gtextFBestFit  , T_BOOL, "0", false  ); // [FALSE]   Scale text laid out on a path to fit the path.
        add(  "gtextFNormalize" , _gtextFNormalize  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "gtextFDxMeasure" , _gtextFDxMeasure  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "gtextFBold"  , _gtextFBold , T_BOOL, "0", false  ); // [FALSE]   Bold font (if available).
        add(  "gtextFItalic"  , _gtextFItalic , T_BOOL, "0", false  ); // [FALSE]   Italic font (if available).
        add(  "gtextFUnderline" , _gtextFUnderline  , T_BOOL, "0", false  ); // [FALSE]   Underline font (if available).
        add(  "gtextFShadow"  , _gtextFShadow , T_BOOL, "0", false  ); // [FALSE]   Shadow font (if available).
        add(  "gtextFSmallcaps" , _gtextFSmallcaps  , T_BOOL, "0", false  ); // [FALSE]   Small caps font (if available).
        add(  "gtextFStrikethrough" , _gtextFStrikethrough  , T_BOOL, "0", false  ); // [FALSE]   Strikethrough font (if available).
        add(  "fGtextOK"  , _fGtextOK , T_BOOL, "0", false  ); // [FALSE]   Text effect (WordArt) supported.
        add(  "gtextFReverseRows" , _gtextFReverseRows  , T_BOOL, "0", false  ); // [FALSE]   Reverse row order.
        add(  "gtextRTF"  , _gtextRTF , T_STRING, null, false  ); // [NULL]    RTF text string.
        add(  "cropFromTop" , _cropFromTop  , T_FIXED, "0", false ); // [0]   Top cropping percentage.
        add(  "cropFromBottom"  , _cropFromBottom , T_FIXED, "0", false ); // [0]   Bottom cropping percentage.
        add(  "cropFromLeft"  , _cropFromLeft , T_FIXED, "0", false ); // [0]   Left cropping percentage.
        add(  "cropFromRight" , _cropFromRight  , T_FIXED, "0", false ); // [0]   Right cropping percentage.
        add(  "pib" , _pib  , T_BINARY, null, false  ); // [NULL]    Binary picture data.
        add(  "pibName" , _pibName  , T_STRING, null, false  ); // [NULL]    Used to link to file pictures.
        add(  "pibFlags"  , _pibFlags , T_VALUE, "0", false  ); // [0]   Flags...
        add(  "pictureTransparent"  , _pictureTransparent , T_COLOR, "0", false ); // [0]   Transparent color.
        add(  "pictureContrast" , _pictureContrast  , T_FIXED, "65536", false ); // [65,536]   Contrast setting.
        add(  "PictureBrightness" , _PictureBrightness  , T_FIXED, "0", false ); // [0]   Brightness setting.
        add(  "pictureGamma"  , _pictureGamma , T_FIXED, "0", false ); // [0]   Gamma correction setting.
        add(  "pictureGray" , _pictureGray  , T_BOOL, "0", false  ); // [0]   Display grayscale.
        add(  "pictureBiLevel"  , _pictureBiLevel , T_BOOL, "0", false  ); // [0]   Display bi-level.
        add(  "pibPrint"  , _pibPrint , T_BINARY, null, false  ); // [NULL]    Blip to display when printing.
        add(  "pibPrintFlags" , _pibPrintFlags  , T_VALUE, "0", false  ); // [0]   Flags...
        add(  "pibPrintName"  , _pibPrintName , T_STRING, null, false  ); // [NULL]   Blip file name.
        add(  "pictureActive" , _pictureActive  , T_BOOL, "0", false  ); // [FALSE]   Server is active (OLE objects only).
        add(  "pictureDblCrMod" , _pictureDblCrMod  , T_COLOR, "0", false ); // [NoChange]   Modification used if shape has double shadow.
        add(  "pictureFillCrMod"  , _pictureFillCrMod , T_COLOR, null, false ); // [Undef]   Modification for BW views.
        add(  "pictureId" , _pictureId  , T_LONG, "0", false  ); // [0]   ID for OLE objects.
        add(  "pictureLineCrMod"  , _pictureLineCrMod , T_COLOR, null, false ); // [Undef]   Modification for BW views.
        add(  "geoLeft" , _geoLeft  , T_LONG, "0", false  ); // [0]   Left of the bounds of a user-drawn shape.
        add(  "geoTop"  , _geoTop , T_LONG, "0", false  ); // [0]   Top of the bounds of a user-drawn shape.
        add(  "geoRight"  , _geoRight , T_LONG, "21600", false  ); // [21,600]    Bounds of a user-drawn shape.
        add(  "geoBottom" , _geoBottom  , T_LONG, "21600", false  ); // [21,600]    Bottom edge of a user-drawn shape.
        add(  "pVerticies"  , _pVerticies , T_ARRAY, null, true ); // [NULL]    The points of the shape.
        add(  "pSegmentInfo"  , _pSegmentInfo , T_ARRAY, null, true ); // [NULL]    The segment information.
        add(  "pFragments"  , _pFragments , T_ARRAY, null, false ); // [NULL]    lists the fragments of the shape.
        add(  "pGuides" , _pGuides  , T_ARRAY, null, false ); // [NULL]    ...
        add(  "pInscribe" , _pInscribe  , T_ARRAY, null, false ); // [NULL]    The inscribed rectangle definition.
        add(  "pAdjustHandles"  , _pAdjustHandles , T_ARRAY, null, false ); // [NULL]   ...
        add(  "adjustValue" , _adjustValue  , T_INT, "0", false ); // [0]   interpretation varies with the shape type.
        add(  "adjust2Value"  , _adjust2Value , T_LONG, "0", false  ); // [0]   Second adjust value.
        add(  "adjust3Value"  , _adjust3Value , T_LONG, "0", false  ); // [0]   Third adjust value.
        add(  "adjust4Value"  , _adjust4Value , T_LONG, "0", false  ); // [0]   Fourth adjust value.
        add(  "adjust5Value"  , _adjust5Value , T_LONG, "0", false  ); // [0]   Fifth adjust value.
        add(  "adjust6Value"  , _adjust6Value , T_LONG, "0", false  ); // [0]   Sixth adjust value.
        add(  "adjust7Value"  , _adjust7Value , T_LONG, "0", false  ); // [0]   Seventh adjust value.
        add(  "adjust8Value"  , _adjust8Value , T_LONG, "0", false  ); // [0]   Eighth adjust value.
        add(  "adjust9Value"  , _adjust9Value , T_LONG, "0", false  ); // [0]   Ninth adjust value.
        add(  "adjust10Value" , _adjust10Value  , T_LONG, "0", false  ); // [0]   Tenth adjust value.
        add(  "fRelChangePage"  , _fRelChangePage , T_BOOL, "0", false  ); // [FALSE]   Anchor may change page.
        add(  "fRelFlipH" , _fRelFlipH  , T_BOOL, "0", false  ); // [FALSE]   Ver flip in group, rel, applied after rot.
        add(  "fRelFlipV" , _fRelFlipV  , T_BOOL, "0", false  ); // [FALSE]   Hor flip in group, rel, applied after rot.
        add(  "groupBottom" , _groupBottom  , T_TWIPS, "20000", true ); // [20,000]    ...
        add(  "groupLeft" , _groupLeft  , T_TWIPS, "0", true ); // [0]   ...
        add(  "groupRight"  , _groupRight , T_TWIPS, "20000", true ); // [20,000]    See groupLeft.
        add(  "groupTop"  , _groupTop , T_TWIPS, "0", true ); // [0]   See groupBottom.
        add(  "relBottom" , _relBottom  , T_TWIPS, "1", true ); // [1]   ...
        add(  "relLeft" , _relLeft  , T_TWIPS, "0", true ); // [0]   ...
        add(  "relRight"  , _relRight , T_TWIPS, "1", true ); // [1]   ...
        add(  "relRotation" , _relRotation  , T_FIXED, "0", false ); // [0]   ...
        add(  "relTop"  , _relTop , T_TWIPS, "0", true ); // [0]   top of shape within parent shape
        add(  "lidRegroup"  , _lidRegroup , T_LONG, "0", false  ); // [0]   Regroup ID.
        add(  "fillType"  , _fillType , T_VALUE, "0", true  ); // [0]   Type of fill (FILL_*)
        add(  "fillColor" , _fillColor  , T_COLOR, "16777215", true ); // [White]   Foreground color.
        add(  "fillOpacity" , _fillOpacity  , T_FIXED, "65536", false ); // [65,536]    Opacity.
        add(  "fillBackColor" , _fillBackColor  , T_COLOR, "16777215", false ); // [White]   Background color.
        add(  "fillBackOpacity" , _fillBackOpacity  , T_FIXED, "65536", false ); // [65,536]   Opacity for shades only.
        add(  "fillBlip"  , _fillBlip , T_BINARY, null, false  ); // [NULL]    Pattern or texture picture for the fill.
        add(  "fillBlipName"  , _fillBlipName , T_STRING, null, false  ); // [NULL]   Picture file name for custom fills.
        add(  "fillblipflags" , _fillblipflags  , T_VALUE, "0", false  ); // [0]   Flags for fills.
        add(  "fillWidth" , _fillWidth  , T_EMU, "0", false ); // [0]   ...
        add(  "fillHeight"  , _fillHeight , T_EMU, "0", false ); // [0]   ...
        add(  "fillAngle" , _fillAngle  , T_FIXED, "0", false ); // [0]   Fade angle specified number of degrees.
        add(  "fillFocus" , _fillFocus  , T_VALUE, "0", false  ); // [0]   Linear shaded fill focus percent.
        add(  "fillToLeft"  , _fillToLeft , T_FIXED, "0", false ); // [0]   ...
        add(  "fillToTop" , _fillToTop  , T_FIXED, "0", false ); // [0]   See fillToLeft.
        add(  "fillToRight" , _fillToRight  , T_FIXED, "0", false ); // [0]   See fillToLeft.
        add(  "fillToBottom"  , _fillToBottom , T_FIXED, "0", false ); // [0]   See fillToLeft.
        add(  "fillShadeColors" , _fillShadeColors  , T_ARRAY, null, false ); // [NULL]   Custom colors for graduated fills.
        add(  "fillOriginX" , _fillOriginX  , T_FIXED, "0", false ); // []    ...
        add(  "fillOriginY" , _fillOriginY  , T_FIXED, "0", false ); // [0]   See fillOriginX.
        add(  "fillShapeOriginX"  , _fillShapeOriginX , T_FIXED, "0", false ); // [0]   See fillOriginX.
        add(  "fillShapeOriginY"  , _fillShapeOriginY , T_FIXED, "0", false ); // [0]   See fillOriginX.
        add(  "fFilled" , _fFilled  , T_BOOL, "1", true  ); // [TRUE]    The shape is filled.
        add(  "fillCrMod" , _fillCrMod  , T_COLOR, null, false ); // [Undefined]   Modification for BW views
        add(  "fillDztype"  , _fillDztype , T_VALUE, "0", false  ); // [0]   Measurement type (MEAS_*)
        add(  "fillRectBottom"  , _fillRectBottom , T_EMU, "0", false ); // [0]   ...
        add(  "fillRectLeft"  , _fillRectLeft , T_EMU, "0", false ); // [0]   ...
        add(  "fillRectRight" , _fillRectRight  , T_EMU, "0", false ); // [0]   ...
        add(  "fillRectTop" , _fillRectTop  , T_EMU, "0", false ); // [0]   ...
        add(  "fillShadePreset" , _fillShadePreset  , T_LONG, "0", false  ); // [0]   Special shades.
        add(  "fillShadeType" , _fillShadeType  , T_VALUE, null, false  ); // [Default]   Type of shading, if using a gradient fill.
        add(  "fillShape" , _fillShape  , T_BOOL, "1", false  ); // [TRUE]    Register pattern on shape.
        add(  "fillUseRect" , _fillUseRect  , T_BOOL, "0", false  ); // [FALSE]   Use the large rectangle.
        add(  "fFillOK" , _fFillOK  , T_BOOL, "1", false  ); // [TRUE]    ...
        add(  "fFillShadeShapeOK" , _fFillShadeShapeOK  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "lineColor" , _lineColor  , T_COLOR, "0", true ); // [Black]   Color of the line.
        add(  "lineBackColor" , _lineBackColor  , T_COLOR, "16777215", false ); // [White]   Background color of the pattern.
        add(  "lineType"  , _lineType , T_VALUE, "0", true  ); // [0]   Type of line (LINE_*)
        add(  "lineFillBlip"  , _lineFillBlip , T_BINARY, null, false  ); // [NULL]   Pattern for the line.
        add(  "lineFillBlipFlags" , _lineFillBlipFlags  , T_VALUE, "0", false  ); // [0]   Flags for patterned lines...
        add(  "lineFillWidth" , _lineFillWidth  , T_EMU, "0", false ); // [0]   Width of the pattern.
        add(  "lineFillHeight"  , _lineFillHeight , T_EMU, "0", false ); // [0]   Height of the pattern.
        add(  "lineWidth" , _lineWidth  , T_EMU, "9525", true ); // [9,525 (0.75pt)]   Width of the line.
        add(  "lineStyle" , _lineStyle  , T_VALUE, "0", true  ); // [0]   Line style (LINES_*)
        add(  "lineDashing" , _lineDashing  , T_VALUE, "0", false  ); // [0]   Dashing
        add(  "lineStartArrowhead"  , _lineStartArrowhead , T_VALUE, "0", true  ); // [0]   ARROW_*
        add(  "lineEndArrowhead"  , _lineEndArrowhead , T_VALUE, "0", true  ); // [0]   End arrow type
        add(  "lineStartArrowWidth" , _lineStartArrowWidth  , T_VALUE, "1", true  ); // [1]   Start arrow width (ARROW_WID_*)
        add(  "lineStartArrowLength"  , _lineStartArrowLength , T_VALUE, "1", true  ); // [1]   Start arrow length (ARROW_LEN_*)
        add(  "lineEndArrowWidth" , _lineEndArrowWidth  , T_VALUE, "1", true  ); // [1]   End arrow width.
        add(  "lineEndArrowLength"  , _lineEndArrowLength , T_VALUE, "1", true  ); // [1]   End arrow length.
        add(  "fLine" , _fLine  , T_BOOL, "1", true  ); // [TRUE]    Has a line.
        add(  "lineCrMod" , _lineCrMod  , T_COLOR, null, false ); // [Undef]   Modification for Black and White views.
        add(  "lineDashStyle" , _lineDashStyle  , T_ARRAY, null, false ); // [NULL]   Line dash style.
        add(  "lineEndCapStyle" , _lineEndCapStyle  , T_VALUE, "2", false  ); // [2]   Line cap style for shape (LINE_CAP_*)
        add(  "lineFillBlipName"  , _lineFillBlipName , T_STRING, null, false  ); // [NULL]   Blip file name.
        add(  "lineFillDztype"  , _lineFillDztype , T_VALUE, "0", false  ); // [0]   fillWidth/Height numbers (MEAS_*)
        add(  "lineJoinStyle" , _lineJoinStyle  , T_VALUE, "2", false  ); // [2]   Line join style for shape (JOIN_*)
        add(  "lineMiterLimit"  , _lineMiterLimit , T_FIXED, "524288", false ); // [524,288]   Ratio of width.
        add(  "fLineOK" , _fLineOK  , T_BOOL, "1", false  ); // [TRUE]    Line style may be set.
        add(  "shadowType"  , _shadowType , T_VALUE, "0", false  ); // [0]   Type of shadow (SHADOW_*)
        add(  "shadowColor" , _shadowColor  , T_COLOR, "12632256", false ); // [#C0C0C0]   Foreground color.
        add(  "shadowHighlight" , _shadowHighlight  , T_COLOR, "13355979", false ); // [#CBCBCB]   Embossed color.
        add(  "shadowOpacity" , _shadowOpacity  , T_FIXED, "65536", false ); // [65,536]   Opacity of the shadow.
        add(  "shadowOffsetX" , _shadowOffsetX  , T_EMU, "0", false ); // [0]   Shadow offset toward the right.
        add(  "shadowOffsetY" , _shadowOffsetY  , T_EMU, "0", false ); // [0]   Shadow offset toward the bottom.
        add(  "shadowSecondOffsetX" , _shadowSecondOffsetX  , T_EMU, "25400", false ); // [25,400]   Double shadow offset toward the right.
        add(  "shadowSecondOffsetY" , _shadowSecondOffsetY  , T_EMU, "25400", false ); // [25,400]   Double shadow offset toward the bottom.
        add(  "shadowScaleXToX" , _shadowScaleXToX  , T_FIXED, "65536", false ); // [65,536]   matrix to generate the shadow.
        add(  "shadowScaleYToX" , _shadowScaleYToX  , T_FIXED, "0", false ); // [0]   See shadowScaleXToX.
        add(  "shadowScaleXToY" , _shadowScaleXToY  , T_FIXED, "0", false ); // [0]   See shadowScaleXToX.
        add(  "shadowScaleYToY" , _shadowScaleYToY  , T_FIXED, "65536", false ); // [65,536]   See shadowScaleXToX.
        add(  "shadowPerspectiveX"  , _shadowPerspectiveX , T_FIXED, "0", false ); // [0]   See shadowScaleXToX.
        add(  "shadowPerspectiveY"  , _shadowPerspectiveY , T_FIXED, "0", false ); // [0]   See shadowScaleXToX.
        add(  "shadowWeight"  , _shadowWeight , T_FIXED, "32768", false ); // [32,768]   See shadowScaleXToX.
        add(  "shadowOriginX" , _shadowOriginX  , T_FIXED, "0", false ); // [0]   ...
        add(  "ShadowOriginY" , _ShadowOriginY  , T_FIXED, "0", false ); // [0]   See shadowOriginX.
        add(  "fShadow" , _fShadow  , T_BOOL, "0", false  ); // [FALSE]   Turns the shadow on or off.
        add(  "shadowCrMod" , _shadowCrMod  , T_COLOR, null, false ); // [Undef]   Modification for BW views.
        add(  "fshadowObscured" , _fshadowObscured  , T_BOOL, "0", false  ); // [FALSE]   Microsoft Excel 5 style shadow.
        add(  "fShadowOK" , _fShadowOK  , T_BOOL, "1", false  ); // [TRUE]    Shadow may be set.
        add(  "c3DSpecularAmt"  , _c3DSpecularAmt , T_FIXED, "0", false ); // [0]   Specular amount for the material.
        add(  "c3DDiffuseAmt" , _c3DDiffuseAmt  , T_FIXED, "65536", false ); // [65,536]   Diffusion amount for the material.
        add(  "c3DShininess"  , _c3DShininess , T_LONG, "5", false  ); // [5]   Shininess of the material.
        add(  "c3DEdgeThickness"  , _c3DEdgeThickness , T_EMU, "12700", false ); // [12,700]   Specular edge thickness.
        add(  "c3DExtrudeForward" , _c3DExtrudeForward  , T_EMU, "0", false ); // [0]   Extrusion amount forward.
        add(  "c3DExtrudeBackward"  , _c3DExtrudeBackward , T_EMU, "457200", false ); // [457,200]   Extrusion amount backward.
        add(  "c3DExtrusionColor" , _c3DExtrusionColor  , T_COLOR, null, false ); // []   Color of the extrusion.
        add(  "f3D" , _f3D  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "fc3DMetallic"  , _fc3DMetallic , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "fc3DUseExtrusionColor" , _fc3DUseExtrusionColor  , T_BOOL, "0", false ); // [FALSE]   Extrusion color is set explicitly.
        add(  "fc3DLightFace" , _fc3DLightFace  , T_BOOL, "1", false  ); // [TRUE]   Light the face of the shape.
        add(  "c3DYRotationAngle" , _c3DYRotationAngle  , T_ANGLE, "0", false ); // [0]   ..
        add(  "c3DXRotationAngle" , _c3DXRotationAngle  , T_ANGLE, "0", false ); // [0]   Degrees about x-axis.
        add(  "c3DRotationAxisX"  , _c3DRotationAxisX , T_LONG, "100", false  ); // [100]   ...
        add(  "c3DRotationAxisY"  , _c3DRotationAxisY , T_LONG, "0", false  ); // [0]   See c3DYRotationAxisX.
        add(  "c3DRotationAxisZ"  , _c3DRotationAxisZ , T_LONG, "0", false  ); // [0]   See c3DYRotationAxisX.
        add(  "c3DRotationAngle"  , _c3DRotationAngle , T_ANGLE, "0", false ); // [0]   ...
        add(  "fC3DRotationCenterAuto"  , _fC3DRotationCenterAuto , T_BOOL, "0", false ); // [FALSE]   ...
        add(  "c3DRotationCenterX"  , _c3DRotationCenterX , T_FIXED, "0", false ); // [0]   ...
        add(  "c3DRotationCenterY"  , _c3DRotationCenterY , T_FIXED, "0", false ); // [0]   Rotation center (Y).
        add(  "c3DRotationCenterZ"  , _c3DRotationCenterZ , T_EMU, "0", false ); // [0]   See c3DRotationCenterY.
        add(  "c3DRenderMode" , _c3DRenderMode  , T_LONG, null, false  ); // [None]   (RENDER_*)
        add(  "c3DXViewpoint" , _c3DXViewpoint  , T_EMU, "1250000", false ); // [1,250,000]   X view point.
        add(  "c3DYViewpoint" , _c3DYViewpoint  , T_EMU, "-1250000", false ); // [-1,250,000]   Y view point.
        add(  "c3DZViewpoint" , _c3DZViewpoint  , T_EMU, "9000000", false ); // [9,000,000]   Z view distance.
        add(  "c3DOriginX"  , _c3DOriginX , T_FIXED, "32768", false ); // [32,768]    ...
        add(  "c3DOriginY"  , _c3DOriginY , T_FIXED, "-32768", false ); // [-32,768]   See c3DOriginX.
        add(  "c3DSkewAngle"  , _c3DSkewAngle , T_FIXED, "-8847360", false ); // [-8,847,360]   Skew angle.
        add(  "c3DSkewAmount" , _c3DSkewAmount  , T_LONG, "50", false  ); // [50]    Percentage skew amount.
        add(  "c3DAmbientIntensity" , _c3DAmbientIntensity  , T_FIXED, "20000", false ); // [20,000]   ...
        add(  "c3DKeyX" , _c3DKeyX  , T_LONG, "50000", false  ); // [50,000]    ...
        add(  "c3DKeyY" , _c3DKeyY  , T_LONG, "0", false  ); // [0]   See c3DKeyX.
        add(  "c3DKeyZ" , _c3DKeyZ  , T_LONG, "10000", false  ); // [10,000]    See c3DKeyX.
        add(  "c3DKeyIntensity" , _c3DKeyIntensity  , T_FIXED, "38000", false ); // [38,000]   Fixed point intensity.
        add(  "c3DFillX"  , _c3DFillX , T_LONG, "-50000", false  ); // [-50,000]   Fill light source direction.
        add(  "c3DFillY"  , _c3DFillY , T_LONG, "0", false  ); // [0]   See c3DfillX.
        add(  "c3DFillZ"  , _c3DFillZ , T_LONG, "10000", false  ); // [10,000]    See c3DfillX.
        add(  "c3DFillIntensity"  , _c3DFillIntensity , T_FIXED, "38000", false ); // [38,000]   ...
        add(  "fc3DParallel"  , _fc3DParallel , T_BOOL, "1", false  ); // [TRUE]    True if the fill has parallel projection.
        add(  "fc3DKeyHarsh"  , _fc3DKeyHarsh , T_BOOL, "1", false  ); // [TRUE]    ...
        add(  "fc3DFillHarsh" , _fc3DFillHarsh  , T_BOOL, "0", false  ); // [FALSE]   ...
        add(  "c3DCrMod"  , _c3DCrMod , T_COLOR, null, false ); // [Undefined]   Modification for BW views.
        add(  "c3DTolerance"  , _c3DTolerance , T_FIXED, "30000", false ); // [30,000]   3D tolerance.
        add(  "f3DOK" , _f3DOK  , T_BOOL, "1", false  ); // [TRUE]    3D can be set.
        add(  "fc3DConstrainRotation" , _fc3DConstrainRotation  , T_BOOL, "1", false ); // [TRUE]   ...
        add(  "perspectiveOffsetX"  , _perspectiveOffsetX , T_FIXED, "0", false ); // [0]   Define a transformation matrix.
        add(  "perspectiveOffsetY"  , _perspectiveOffsetY , T_FIXED, "0", false ); // [0]   See perspectiveOffsetX.
        add(  "perspectiveOriginX"  , _perspectiveOriginX , T_FIXED, "32768", false ); // [32,768]   Perspective x origin.
        add(  "perspectiveOriginY"  , _perspectiveOriginY , T_FIXED, "32768", false ); // [32,768]   Perspective y origin.
        add(  "perspectivePerspectiveX" , _perspectivePerspectiveX  , T_FIXED, "0", false ); // [0]   See perspectiveOffsetX.
        add(  "perspectivePerspectiveY" , _perspectivePerspectiveY  , T_FIXED, "0", false ); // [0]   See perspectiveOffsetX.
        add(  "perspectiveScaleXToX"  , _perspectiveScaleXToX , T_FIXED, "65536", false ); // [65,536]   See perspectiveOffsetX.
        add(  "perspectiveScaleXToY"  , _perspectiveScaleXToY , T_FIXED, "0", false ); // [0]   See perspectiveOffsetX.
        add(  "perspectiveScaleYToX"  , _perspectiveScaleYToX , T_FIXED, "0", false ); // [0]   See perspectiveOffsetX.
        add(  "perspectiveScaleYToY"  , _perspectiveScaleYToY , T_FIXED, "65536", false ); // [65,536]   See perspectiveOffsetX.
        add(  "perspectiveType" , _perspectiveType  , T_VALUE, "1", false  ); // [1]   Where transform applies (TRANSFORM_*)
        add(  "perspectiveWeight" , _perspectiveWeight  , T_FIXED, "256", false ); // [256]   Scaling factor.
        add(  "fPerspective"  , _fPerspective , T_BOOL, "0", false  ); // [None]    On/off.
        add(  "spcot" , _spcot  , T_VALUE, "3", false  ); // [3]   CALLOUT_TYPE_*
        add(  "dxyCalloutGap" , _dxyCalloutGap  , T_EMU, "76200", false ); // [76,200]   Distance from box to first point.
        add(  "spcoa" , _spcoa  , T_VALUE, "1", false  ); // [1]   CO_ANGLE_*
        add(  "spcod" , _spcod  , T_VALUE, "3", false  ); // [3]   CO_DROP_*
        add(  "dxyCalloutDropSpecified" , _dxyCalloutDropSpecified  , T_EMU, "114300", false ); // [114,300]   ...
        add(  "dxyCalloutLengthSpecified" , _dxyCalloutLengthSpecified  , T_EMU, "0", false ); // [0]   ...
        add(  "fCallout"  , _fCallout , T_BOOL, "0", false  ); // [FALSE]   This is a callout.
        add(  "fCalloutAccentBar" , _fCalloutAccentBar  , T_BOOL, "0", false  ); // [FALSE]   Callout has an accent bar.
        add(  "fCalloutTextBorder"  , _fCalloutTextBorder , T_BOOL, "1", false  ); // [TRUE]   Callout has a text border.
        add(  "fCalloutDropAuto"  , _fCalloutDropAuto , T_BOOL, "0", false  ); // [FALSE]   True if Auto attach is on
        add(  "fCalloutLengthSpecified" , _fCalloutLengthSpecified  , T_BOOL, "0", false ); // [FALSE]   True if the callout length is specified
        add(  "fCalloutMinusX"  , _fCalloutMinusX , T_BOOL, "0", false  ); // [FALSE]   The polyline of the callout is to the right
        add(  "fCalloutMinusY"  , _fCalloutMinusY , T_BOOL, "0", false  ); // [FALSE]   The polyline of the callout is down.
        add(  "cxk" , _cxk  , T_VALUE, "1", false  ); // [1]   Connection site type (CONN_SITE_*)
        add(  "cxstyle" , _cxstyle  , T_VALUE, "3", false  ); // [3]   Connector style (CONN_STYLE_*)
        add(  "bWMode"  , _bWMode , T_VALUE, "1", false  ); // [1]   Modifications made in b/w modes (BW_*)
        add(  "bWModeBW"  , _bWModeBW , T_VALUE, "1", false  ); // [1]   See bWMode.
        add(  "bWModePureBW"  , _bWModePureBW , T_VALUE, "1", false  ); // [1]   See bWmode.
    }

    static {
        setupTags();
    }
}
