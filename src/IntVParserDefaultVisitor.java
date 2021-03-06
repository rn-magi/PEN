/* Generated By:JavaCC: Do not edit this line. IntVParserDefaultVisitor.java Version 6.1_2 */
public class IntVParserDefaultVisitor implements IntVParserVisitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIntVUnit node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTErrorOccur node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFunction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBlock node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFunctionVar node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTVarDecl node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDecl node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTArray node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAssignStats node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAssignStat node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIfStat node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTRepeatUntil node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTForStatAdd node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTWhileSwitchFor node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCase node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTInfiniteLoop node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLabel node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGetStat node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTPutStat node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTORNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTANDNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTNOTNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTEQNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTNTNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLSNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGTNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLENode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGENode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAddNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSubNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMulNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDivNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSurNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMinNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGet node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTRandom node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSine node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCosine node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTTangent node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSqrt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFloor node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCeil node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTRound node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAbs node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLog node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTInt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLong node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLength node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAppend node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSubstring node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTInsert node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTReplace node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTExtract node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTStr2Int node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTInt2Str node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCompare node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgOpenWindow node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgOpenGraphWindow node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgCloseWindow node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgClearWindow node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSaveWindow node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetOrigin node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetMap node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetFillColor node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetLineColor node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetTextColor node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetFont node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetFontType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetFontSize node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetDotShape node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetArrowDir node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetArrowType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetLineShape node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetLineWidth node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawPoint node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawLine node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawText node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawCircle node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgFillCircle node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawOval node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgFillOval node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawBox node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgFillBox node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawArc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgFillArc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawPolygon node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgFillPolygon node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawPolyline node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgDrawImage node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgSetRepaintFlag node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTgRepaint node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_openr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_openw node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_opena node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_close node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_getstr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_getline node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_putstr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_putline node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_flush node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_isfile node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_rename node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFile_remove node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSleep node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIdent node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTArrayNum node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFunctionCall node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTReturn node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBreak node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTReturnProcedural node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLiteral node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFloatLiteral node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTTrue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTFalse node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTStrlit node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTEXTRA_STR node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=1dd1a5f79fe0462fe057db416ed9bac2 (do not edit this line) */
