
package rossi.dfp;

public class dfpcalc
{
  public static void main(String args[]) throws Exception
  {
    dfp a, b, tmp;
    dfp r[];
    String str;
    String dec_a;
    String dec_b;
    String dec_r1;
    String dec_r2;
    String dec_r3;
    String dec_r4;
    String dfpClass = System.getProperty("dfp.class", "rossi.dfp.dfp");
    boolean display_on = true;
    int operations = 0;
    java.io.DataInputStream in = new java.io.DataInputStream(System.in);

    if (args.length > 0 && args[0].charAt(0)=='n')
      display_on = false;

    a=(dfp)Class.forName(dfpClass).newInstance();
    b=a.newInstance("0");
    r = new dfp[4];
    r[0] = a.newInstance("0");
    r[1] = a.newInstance("0");
    r[2] = a.newInstance("0");
    r[3] = a.newInstance("0");

    if (display_on)
    {
      System.out.print("\033[2J\1b[H\n\n");

      System.out.print("\n\n\n\n\n\n\n\n\n\nCommands:\n"+
            "+ B=A+B      - B=A/B       * B=A*B       / B=A/B\n"+
            "N B=-B       S B=sqrt(B) \n"+
            "X A=B, B=A\n"+
            "D A=B\n"+
            "Pn Rn=B  Store B in register n\n"+
            "Rn B=Rn  Copy register n to B \n"+
            "Q Quit\n");
    }             

    do
    {
      if (display_on)
      {
        dec_a = a.toString(); 
        dec_b = b.toString(); 
        dec_r1 = r[0].toString(); 
        dec_r2 = r[1].toString(); 
        dec_r3 = r[2].toString(); 
        dec_r4 = r[3].toString(); 

        System.out.print("\033[H"+
              "R1 = "+dec_r1+"\033[K\n"+
              "R2 = "+dec_r2+"\033[K\n"+
              "R3 = "+dec_r3+"\033[K\n"+
              "R4 = "+dec_r4+"\033[K\n"+
              "-------------------------\033[K\n"+
              "A  = "+dec_a+"\033[K\n"+
              "B  = "+dec_b+"\033[K\n"+
              "-------------------------\033[K\n"+
              ">\033[K");
      }
      str = in.readLine().toUpperCase();
      if (str.length() == 0)
        str = "0";

      switch (str.charAt(0))
      {
        case '+':
              b = b.add(a);
              operations++;
              break;

        case '-':
              b = a.subtract(b);
              operations++;
              break;

        case '*':
              b = b.multiply(a);
              operations++;
              break;

        case '/':
              b = a.divide(b);
              operations++;
              break;

        case '^':
              b = dfpmath.pow(a, b);
              operations++;
              break;

        case 'L':
              b = dfpmath.ln(b);
              break;

        case 'E':
              b = dfpmath.exp(b);
              break;

        case 'S':
              b = b.sqrt();
              break;

        case 'I':
              b = dfpmath.sin(b);
              break;

        case 'O':
              b = dfpmath.cos(b);
              break;

        case 'T':
              b = dfpmath.tan(b);
              break;

        case 'A':
              b = dfpmath.atan(b);
              break;
   
/*
        case 'W':
               System.out.println("\n\n"+
                      Integer.toHexString(b.Mant.data[0])+" "+
                      Integer.toHexString(b.Mant.data[1])+" "+
                      Integer.toHexString(b.Mant.data[2])+" "+
                      Integer.toHexString(b.Mant.data[3])+" "+
                      b.Exp);
               break;
*/

        case 'X':
              tmp = b;
              b = a;
              a = tmp;
              break;

        case 'D':
              a = a.newInstance(b);
              break;

        case 'N':
              b = b.negate();
              break;

        case 'P':
              r[(str.charAt(1)-'0'-1) & 3] = a.newInstance(b);
              break;

        case 'R':
              b = a.newInstance(r[(str.charAt(1)-'0'-1) & 3]);
              break;

        default:
              a = a.newInstance(b);
              b = a.newInstance(str);
              break;
      }
      
    } while(str.charAt(0) != 'Q');

/*
    if (!display_on)
    {
      start_time = (time(NULL) - start_time);
      printf("Done %d operations. time = %d seconds. FLOPS = %d\n\n\n", operations, start_time, operations/start_time);
      a.dfp2ascii(dec_a);
      b.dfp2ascii(dec_b);
      r[0].dfp2ascii(dec_r1);
      r[1].dfp2ascii(dec_r2);
      r[2].dfp2ascii(dec_r3);
      r[3].dfp2ascii(dec_r4);

      printf(
             "R1 = %s\n"
             "R2 = %s\n"
             "R3 = %s\n"
             "R4 = %s\n"
             "-------------------------\n"
             "A  = %s\n"
             "B  = %s\n"
             "-------------------------\n"
             ">", dec_r1, dec_r2, dec_r3, dec_r4, dec_a, dec_b);
    }
*/

  }
}
