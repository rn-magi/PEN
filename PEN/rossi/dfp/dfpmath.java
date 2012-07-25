
package rossi.dfp;

/** Mathematical routines and constants for use with dfp.  Constants are 
 *  defined with in dfpconstants.java
 */

public class dfpmath implements dfpconstants
{
  /** sqrt(2) */
  public final static dfp SQR2 = new dfp(STR_SQR2);
  /** sqrt(2) in 2 pieces */
  public final static dfp[] SQR2_SPLIT = split(STR_SQR2);
  /** sqrt(2)/2 */
  public final static dfp SQR2_2 = new dfp(STR_SQR2_2);
  /** sqrt(3) */
  public final static dfp SQR3 = new dfp(STR_SQR3);
  /** sqrt(3)/3 */
  public final static dfp SQR3_3 = new dfp(STR_SQR3_3);
  /** PI */
  public final static dfp PI = new dfp(STR_PI);
  /** PI_SPLIT in 2 pieces */
  public final static dfp[] PI_SPLIT = split(STR_PI);
  /** E */
  public final static dfp E = new dfp(STR_E);
  /** E_SPLIT  The number e split in two pieces */
  public final static dfp[] E_SPLIT = split(STR_E);
  /** ln(2) */
  public final static dfp LN2 = new dfp(STR_LN2);
  /** LN2_SPLIT  The number e split in two pieces */
  public final static dfp[] LN2_SPLIT = split(STR_LN2);
  /** ln(5) */
  public final static dfp LN5 = new dfp(STR_LN5);
  /** LN5_SPLIT  The number e split in two pieces */
  public final static dfp[] LN5_SPLIT = split(STR_LN5);
  /** ln(10) */
  public final static dfp LN10 = new dfp(STR_LN10);

  /** Breaks a string representation up into two dfp's such 
   * that the sum of them is equivilent to the input string, but
   * has higher precision than using a single dfp.  Useful for
   * improving accuracy of exponentination and critical multplies */
  protected static dfp[] split(String a)
  {
    dfp result[] = new dfp[2];
    char[] buf;
    boolean leading = true;
    int sp = 0;
    int sig = 0;

    buf = new char[a.length()];

    for (int i=0; i<buf.length; i++)
    {
      buf[i] = a.charAt(i);

      if (buf[i] >= '1' && buf[i] <= '9')
        leading = false;

      if (buf[i] == '.')
      {
        sig += ((400-sig) % 4);
        leading = false;
      }

      if (sig == (dfp.DIGITS/2) * 4)
      {
        sp = i;
        break;
      }

      if (buf[i] >= '0' && buf[i] <= '9' && !leading)
        sig ++;
    }

    result[0] = new dfp(new String(buf, 0, sp));

    for (int i=0; i<buf.length; i++)
    {
      buf[i] = a.charAt(i);
      if (buf[i] >= '0' && buf[i] <= '9' && i < sp)
        buf[i] = '0';
    }

    result[1] = new dfp(new String(buf));

    return result;
  }

  /** Splits a dfp into 2 dfp's such that their sum is equal to the
   *  input dfp */
  protected static dfp[] split(dfp a)
  {
    dfp[] result;
    dfp shift;
    int ex;

    result = new dfp[2];

/*
    ex = a.log10K();
    shift = a.power10K(dfp.DIGITS/2-ex-1);
    result[0] = a.multiply(shift).rint().divide(shift);
    result[1] = a.subtract(result[0]);
*/
    shift = a.multiply(a.power10K(dfp.DIGITS/2));
    result[0] = a.add(shift).subtract(shift);
    result[1] = a.subtract(result[0]);

    return result;
  }

  /** Multiply two numbers that are split in to two pices that are
   *  meant to be added together.  Use binomail multiplication so
   *  ab = a0 b0 + a0 b1 + a1 b0 + a1 b1
   *  Store the first term in result0, the rest in result1
   */
  protected static dfp[] splitMult(dfp[] a, dfp[] b)
  {
    dfp[] result = new dfp[2];

    result[1] = dfp.zero;
    result[0] = a[0].multiply(b[0]);

    /* If result[0] is infinite or zero, don't compute result[1].  
     * Attempting to do so may produce NaNs.
     */

    if (result[0].classify() == dfp.INFINITE || result[0].equal(result[1]))
      return result;

    result[1] = a[0].multiply(b[1]).add(a[1].multiply(b[0])).add(a[1].multiply(b[1]));

    return result;
  }

  /** Divide two numbers that are split in to two pices that are
   *  meant to be added together.  Inverse of split mult above:
   *  
   *  (a+b) / (c+d) = (a/c) + ( (bc-ad)/(c**2+cd) )
   */
  protected static dfp[] splitDiv(dfp[] a, dfp[] b)
  {
    dfp[] result;

    result = new dfp[2];
  
    result[0] = a[0].divide(b[0]);
    result[1] = a[1].multiply(b[0]).subtract(a[0].multiply(b[1]));
    result[1] = result[1].divide(b[0].multiply(b[0]).add(b[0].multiply(b[1])));
   
    return result;
  }

  /** Raise a split base to the a power.  return a combined result */
  protected static dfp splitPow(dfp[] base, int a)
  {
    int trial, prevtrial;
    dfp[] result, r;
    boolean invert = false;

    r = new dfp[2];

    result = new dfp[2];
    result[0] = dfp.one;
    result[1] = dfp.zero;

    if (a == 0)       /* Special case a = 0 */
      return result[0].add(result[1]);

    if (a < 0)   /* If a is less than zero */
    {
      invert = true;
      a = -a;
    }

    /* Exponentiate by successive squaring */
    do
    {
      r[0] = new dfp(base[0]);
      r[1] = new dfp(base[1]);
      trial = 1;
      
      while(true)
      {
        prevtrial = trial;
        trial = trial * 2;
        if (trial > a)
          break;
        r = splitMult(r, r);
      }

      trial = prevtrial;

      a = a - trial;
      result = splitMult(result, r);

    } while (a >= 1);

    result[0] = result[0].add(result[1]);

    if (invert)
      result[0] = dfp.one.divide(result[0]);

    return result[0];
  }

  /** Raises base to the power a by successive squaring */
  public static dfp pow(dfp base, int a)
  {
    int trial, prevtrial;
    dfp result, r, prevr;
    boolean invert = false;

    result = dfp.one;

    if (a == 0)       /* Special case */
      return result;

    if (a < 0)
    {
      invert = true;
      a = -a;
    }

    /* Exponentiate by successive squaring */
    do
    {
      r = new dfp(base);
      trial = 1;
      
      do
      {
        prevr = new dfp(r);
        prevtrial = trial;
        r = r.multiply(r);
        trial = trial * 2;
      } while (a>trial);

      r = prevr;
      trial = prevtrial;

      a = a - trial;
      result = result.multiply(r);

    } while (a >= 1);

    if (invert)
      result = dfp.one.divide(result);

    return base.newInstance(result);
  }

  /** Computes e to the given power.  a is broken into two parts, 
   *  such that a = n+m  where n is an integer. 
   *
   *  We use pow() to compute e**n and a taylor series to compute
   *  e**m.  We return (e**n)(e**m)
   */
  public static dfp exp(dfp a)
  {
    dfp inta, fraca, einta, efraca;
    int ia;
    dfp result;

    inta = a.rint();
    fraca = a.subtract(inta);

    ia = inta.intValue();
    if (ia > 2147483646)  // return +Infinity
      return a.newInstance(dfp.create((byte)1, (byte) dfp.INFINITE));

    if (ia < -2147483646)  // return 0;
      return a.newInstance(dfp.zero);

    einta = splitPow(E_SPLIT, ia);
    efraca = expInternal(fraca);

    result = einta.multiply(efraca);
    return a.newInstance(result);
  }

  /** Computes e to the given power.  Where -1 < a < 1.  Use the
   *  classic Taylor series.  1 + x**2/2! + x**3/3! + x**4/4!  ...
   */
  protected static dfp expInternal(dfp a)
  {
    int i;
    dfp y, py, x, fact;

    y = dfp.one;
    x = dfp.one;
    fact = dfp.one;
    py = new dfp(y);

    for (i=1; i<90; i++)
    {
      x = x.multiply(a);
      fact = fact.divide(i);
      y = y.add(x.multiply(fact));
      if (y.equal(py))
        break;
      py = new dfp(y);
    }

    return y;
  }

  /** Returns the natural logarithm of a.  a is first split into three
   *  parts such that  a = (10000^h)(2^j)k.  ln(a) is computed by
   *  ln(a) = ln(5)*h + ln(2)*(h+j) + ln(k)
   *  k is in the range 2/3 < k <4/3 and is passed on to a series 
   *  expansion.
   */
  public static dfp ln(dfp a)
  {
    int lr;
    dfp x;
    int ix;
    int p2 = 0;
    dfp spx[], spy[], spz[];

    /* Check the arguments somewhat here */
    if (a.equal(dfp.zero) || a.lessThan(dfp.zero) ||  // negative or zero
       (a.equal(a) == false))                         // or NaN
    {
      dfp.setIEEEFlags(dfp.getIEEEFlags() | dfp.FLAG_INVALID);
      return a.dotrap(dfp.FLAG_INVALID, "ln", a, dfp.create((byte)1, (byte) dfp.QNAN));
    }

    if (a.classify() == dfp.INFINITE)
    {
      return a;
    }

    spx = new dfp[2];
    spy = new dfp[2];
    spz = new dfp[2];
    
    x = new dfp(a);
    lr = x.log10K();

    x = x.divide(pow(new dfp("10000"), lr));  /* This puts x in the range 0-10000 */
    ix = x.floor().intValue();

    while (ix > 2)
    {
      ix >>= 1;     
      p2++;
    }


    spx = split(x);
    spy[0] = pow(dfp.two, p2);          // use spy[0] temporarily as a divisor
    spx[0] = spx[0].divide(spy[0]);
    spx[1] = spx[1].divide(spy[0]);

    spy[0] = new dfp("1.33333");    // Use spy[0] for comparison 
    while (spx[0].add(spx[1]).greaterThan(spy[0]))
    {
      spx[0] = spx[0].divide(2);
      spx[1] = spx[1].divide(2);
      p2++;
    }

    /** X is now in the range of 2/3 < x < 4/3 */

    spz = lnInternal(spx);

    spx[0] = new dfp(new StringBuffer().append(p2+4*lr).toString());
    spx[1] = dfp.zero;
    spy = splitMult(LN2_SPLIT, spx);

    spz[0] = spz[0].add(spy[0]);
    spz[1] = spz[1].add(spy[1]);

    spx[0] = new dfp(new StringBuffer().append(4*lr).toString());
    spx[1] = dfp.zero;
    spy = splitMult(LN5_SPLIT, spx);

    spz[0] = spz[0].add(spy[0]);
    spz[1] = spz[1].add(spy[1]);

    return a.newInstance(spz[0].add(spz[1]));
  }

  /** Computes the natural log of a number between 0 and 2 */
  /*
   *  Much better ln(x) algorithm....
   *
   *  Let f(x) = ln(x),
   *
   *  We know that f'(x) = 1/x, thus from Taylor's theorum we have: 
   *
   *           -----          n+1         n
   *  f(x) =   \           (-1)    (x - 1)
   *           /          ----------------    for 1 <= n <= infinity
   *           -----             n
   *
   *  or
   *                       2        3       4
   *                   (x-1)   (x-1)    (x-1)
   *  ln(x) =  (x-1) - ----- + ------ - ------ + ...
   *                     2       3        4
   *
   *  alternatively,
   *
   *                  2    3   4
   *                 x    x   x
   *  ln(x+1) =  x - -  + - - - + ...
   *                 2    3   4
   *
   *  This series can be used to compute ln(x), but it converges too slowly.
   *
   *  If we substitute -x for x above, we get
   *
   *                   2    3    4
   *                  x    x    x
   *  ln(1-x) =  -x - -  - -  - - + ...
   *                  2    3    4
   *
   *  Note that all terms are now negative.  Because the even powered ones 
   *  absorbed the sign.  Now, subtract the series above from the previous
   *  one to get ln(x+1) - ln(1-x).  Note the even terms cancel out leaving 
   *  only the odd ones
   *
   *                             3     5      7
   *                           2x    2x     2x
   *  ln(x+1) - ln(x-1) = 2x + --- + --- + ---- + ...
   *                            3     5      7
   *
   *  By the property of logarithms that ln(a) - ln(b) = ln (a/b) we have:
   *
   *                                3        5        7
   *      x+1           /          x        x        x          \
   *  ln ----- =   2 *  |  x  +   ----  +  ----  +  ---- + ...  |
   *      x-1           \          3        5        7          /
   *
   *  But now we want to find ln(a), so we need to find the value of x 
   *  such that a = (x+1)/(x-1).   This is easily solved to find that
   *  x = (a-1)/(a+1).
   */
  protected static dfp[] lnInternal(dfp a[])
  {
    dfp x, y, py, num, t;
    int den;

    den = 1;

    /* Now we want to compute x = (a-1)/(a+1) but this is prone to 
     * loss of precision.  So instead, compute x = (a/4 - 1/4) / (a/4 + 1/4)
     */
    t = a[0].divide(4).add(a[1].divide(4));
    x = t.add(new dfp("-0.25")).divide(t.add(new dfp("0.25")));

    y = new dfp(x);
    num = new dfp(x);
    py = new dfp(y);
    for (int i=0; i<10000; i++)
    {
      num = num.multiply(x);
      num = num.multiply(x);
      den = den + 2;
      t = num.divide(den);
      y = y.add(t);
      if (y.equal(py))
	break;
      py = new dfp(y);
    }

    y = y.multiply(dfp.two);

    return split(y);
  }

  /** Computes x to the y power.<p>
   *
   *  Uses the following method:<p>
   * 
   *  <ol>
   *  <li> Set u = rint(y), v = y-u 
   *  <li> Compute a = v * ln(x)
   *  <li> Compute b = rint( a/ln(2) )
   *  <li> Compute c = a - b*ln(2)
   *  <li> x<sup>y</sup> = x<sup>u</sup>  *   2<sup>b</sup> * e<sup>c</sup>
   *  </ol>
   *  if |y| > 1e8, then we compute by exp(y*ln(x))   <p>
   *
   *  <b>Special Cases</b><p>
   *  <ul>
   *  <li>  if y is 0.0 or -0.0 then result is 1.0
   *  <li>  if y is 1.0 then result is x
   *  <li>  if y is NaN then result is NaN
   *  <li>  if x is NaN and y is not zero then result is NaN
   *  <li>  if |x| > 1.0 and y is +Infinity then result is +Infinity
   *  <li>  if |x| < 1.0 and y is -Infinity then result is +Infinity
   *  <li>  if |x| > 1.0 and y is -Infinity then result is +0
   *  <li>  if |x| < 1.0 and y is +Infinity then result is +0
   *  <li>  if |x| = 1.0 and y is +/-Infinity then result is NaN
   *  <li>  if x = +0 and y > 0 then result is +0
   *  <li>  if x = +Inf and y < 0 then result is +0
   *  <li>  if x = +0 and y < 0 then result is +Inf
   *  <li>  if x = +Inf and y > 0 then result is +Inf
   *  <li>  if x = -0 and y > 0, finite, not odd integer then result is +0
   *  <li>  if x = -0 and y < 0, finite, and odd integer then result is -Inf
   *  <li>  if x = -Inf and y > 0, finite, and odd integer then result is -Inf
   *  <li>  if x = -0 and y < 0, not finite odd integer then result is +Inf
   *  <li>  if x = -Inf and y > 0, not finite odd integer then result is +Inf
   *  <li>  if x < 0 and y > 0, finite, and odd integer then result is -(|x|<sup>y</sup>)
   *  <li>  if x < 0 and y > 0, finite, and not integer then result is NaN
   *  </ul>
   */

  public static dfp pow(dfp x, dfp y)
  {
    dfp a, b, c, u, v, r;
    boolean invert = false;
    int ui, bi;

    /* Check for special cases */
    if (y.equal(dfp.zero))
      return x.newInstance(dfp.one);

    if (y.equal(dfp.one))
    {
      if (!x.equal(x))  // Test for NaNs
      {
        dfp.setIEEEFlags(dfp.getIEEEFlags() | dfp.FLAG_INVALID);
        return x.dotrap(dfp.FLAG_INVALID, "pow", x, x);
      }
      return x;
    }

    if (!x.equal(x) || !y.equal(y)) // Test for NaNs
    {
      dfp.setIEEEFlags(dfp.getIEEEFlags() | dfp.FLAG_INVALID);
      return x.dotrap(dfp.FLAG_INVALID, "pow", x, dfp.create((byte)1, (byte) dfp.QNAN));
    }

    // X == 0
    if (x.equal(dfp.zero))
    {
      if (dfp.copysign(dfp.one, x).greaterThan(dfp.zero))  // X == +0
      {
        if (y.greaterThan(dfp.zero))
          return x.newInstance(dfp.zero);
        else
          return x.newInstance(dfp.create((byte)1, (byte)dfp.INFINITE));
      }
      else  // X == -0
      {
        /* If y is odd integer */
        if (y.classify() == dfp.FINITE && y.rint().equal(y) && !y.remainder(dfp.two).equal(dfp.zero))
        {
          if (y.greaterThan(dfp.zero))
            return x.newInstance(dfp.zero.negate());
          else
            return x.newInstance(dfp.create((byte)-1, (byte)dfp.INFINITE));
        }
        else  // Y is not odd integer
        {
          if (y.greaterThan(dfp.zero))
            return x.newInstance(dfp.zero);
          else
            return x.newInstance(dfp.create((byte)1, (byte)dfp.INFINITE));
        }
      }
    }

    if (x.lessThan(dfp.zero))  /* Make x positive, but keep track of it */
    {
      x = x.negate();
      invert = true;
    }

    if (x.greaterThan(dfp.one) && y.classify() == dfp.INFINITE)
    {
      if (y.greaterThan(dfp.zero))
        return y;
      else
        return x.newInstance(dfp.zero);
    }

    if (x.lessThan(dfp.one) && y.classify() == dfp.INFINITE)
    {
      if (y.greaterThan(dfp.zero))
        return x.newInstance(dfp.zero);
      else
        return x.newInstance(dfp.copysign(y, dfp.one));
    }

    if (x.equal(dfp.one) && y.classify() == dfp.INFINITE)
    {
      dfp.setIEEEFlags(dfp.getIEEEFlags() | dfp.FLAG_INVALID);
      return x.dotrap(dfp.FLAG_INVALID, "pow", x, dfp.create((byte)1, (byte) dfp.QNAN));
    }

    if (x.classify() == dfp.INFINITE)  // x = +/- inf
    {
      if (invert)  // negative infinity
      {
        /* If y is odd integer */
        if (y.classify() == dfp.FINITE && y.rint().equal(y) && !y.remainder(dfp.two).equal(dfp.zero))
        {
          if (y.greaterThan(dfp.zero))
            return x.newInstance(dfp.create((byte)-1, (byte)dfp.INFINITE));
          else
            return x.newInstance(dfp.zero.negate());
        }
        else  // Y is not odd integer
        {
          if (y.greaterThan(dfp.zero))
            return x.newInstance(dfp.create((byte)1, (byte)dfp.INFINITE));
          else
            return x.newInstance(dfp.zero);
        }
      }
      else         // positive infinity
      {
        if (y.greaterThan(dfp.zero))
          return x;
        else
          return x.newInstance(dfp.zero);
      }
    }

    if (invert && !y.rint().equal(y))
    {
      dfp.setIEEEFlags(dfp.getIEEEFlags() | dfp.FLAG_INVALID);
      return x.dotrap(dfp.FLAG_INVALID, "pow", x, dfp.create((byte)1, (byte) dfp.QNAN));
    }

    /* End special cases */

    if (y.lessThan(new dfp("1e8")) && y.greaterThan(new dfp("-1e8")))
    {
      u = y.rint();
      ui = u.intValue();

      v = y.subtract(u);

      if (v.unequal(dfp.zero))
      {
        a = v.multiply(ln(x));
        b = a.divide(LN2).rint();
        bi = b.intValue();

        c = a.subtract(b.multiply(LN2));
        r = splitPow(split(x), ui);
        r = r.multiply(pow(dfp.two, b.intValue()));
        r = r.multiply(exp(c));
      }
      else   
      {
        r = splitPow(split(x), ui);
      }
    }
    else   // very large exponent.  |y| > 1e8
    {
      r = exp(ln(x).multiply(y));
    }

    if (invert)
    {
      // if y is odd integer
      if (y.rint().equal(y) && !y.remainder(dfp.two).equal(dfp.zero))
        r = r.negate();
    }

    return x.newInstance(r);
  }

  /** Computes sin(a)  Used when 0 < a < pi/4.  Uses the
   *  classic Taylor series.  x - x**3/3! + x**5/5!  ...
   */
  protected static dfp sinInternal(dfp a[])
  {
    int i;
    dfp c, y, py, x, fact;

    c = a[0].add(a[1]);
    y = c;
    c = c.multiply(c);
    x = y;
    fact = dfp.one;
    py = new dfp(y);

    for (i=3; i<90; i+=2)
    {
      x = x.multiply(c);
      x = x.negate();

      fact = fact.divide((i-1)*i);  // 1 over fact
      y = y.add(x.multiply(fact));
      if (y.equal(py))
        break;
      py = new dfp(y);
    }

    return y;
  }

  /** Computes cos(a)  Used when 0 < a < pi/4.  Uses the
   *  classic Taylor series for cosine.  1 - x**2/2! + x**4/4!  ...
   */
  protected static dfp cosInternal(dfp a[])
  {
    int i;
    dfp y, py, x, c, fact;


    x = dfp.one;
    y = dfp.one;
    c = a[0].add(a[1]);
    c = c.multiply(c);

    fact = dfp.one;
    py = new dfp(y);

    for (i=2; i<90; i+=2)
    {
      x = x.multiply(c);
      x = x.negate();

      fact = fact.divide((i-1)*i);  // 1 over fact

      y = y.add(x.multiply(fact));
      if (y.equal(py))
        break;
      py = new dfp(y);
    }

    return y;
  }

  /** computes the sine of the argument */
  public static dfp sin(dfp a)
  {
    dfp x, y;
    boolean neg = false;

    /* First reduce the argument to the range of +/- PI */
    x = a.remainder(PI.multiply(2));

    /* if x < 0 then apply identity sin(-x) = -sin(x) */
    /* This puts x in the range 0 < x < PI            */
    if (x.lessThan(dfp.zero))
    {
      x = x.negate();
      neg = true;
    }

    /* Since sine(x) = sine(pi - x) we can reduce the range to
     * 0 < x < pi/2
     */

    if (x.greaterThan(PI.divide(2)))
      x = PI.subtract(x);

    if (x.lessThan(PI.divide(4)))
    {
      dfp c[] = new dfp[2];
      c[0] = x;
      c[1] = dfp.zero;

      //y = sinInternal(c);
      y = sinInternal(split(x));
    }
    else
    {
      dfp c[] = new dfp[2];

      c[0] = PI_SPLIT[0].divide(2).subtract(x);
      c[1] = PI_SPLIT[1].divide(2);
      y = cosInternal(c);
    }

    if (neg)
      y = y.negate();

    return a.newInstance(y);
  }

  /** computes the cosine of the argument */
  public static dfp cos(dfp a)
  {
    dfp x, y;
    boolean neg = false;

    /* First reduce the argument to the range of +/- PI */
    x = a.remainder(PI.multiply(2));

    /* if x < 0 then apply identity cos(-x) = cos(x) */
    /* This puts x in the range 0 < x < PI           */
    if (x.lessThan(dfp.zero))
      x = x.negate();

    /* Since cos(x) = -cos(pi - x) we can reduce the range to
     * 0 < x < pi/2
     */

    if (x.greaterThan(PI.divide(2)))
    {
      x = PI.subtract(x);
      neg = true;
    }

    if (x.lessThan(PI.divide(4)))
    {
      dfp c[] = new dfp[2];
      c[0] = x;
      c[1] = dfp.zero;

      y = cosInternal(c);
    }
    else
    {
      dfp c[] = new dfp[2];

      c[0] = PI_SPLIT[0].divide(2).subtract(x);
      c[1] = PI_SPLIT[1].divide(2);
      y = sinInternal(c);
    }

    if (neg)
      y = y.negate();

    return a.newInstance(y);
  }

  /** computes the tangent of the argument */
  public static dfp tan(dfp a)
  {
    return sin(a).divide(cos(a));
  }

  protected static dfp atanInternal(dfp a)
  {
    int i;
    dfp y, py, x;

    y = new dfp(a);
    x = new dfp(y);
    py = new dfp(y);

    for (i=3; i<90; i+=2)
    {
      x = x.multiply(a);
      x = x.multiply(a);
      x = x.negate();
      y = y.add(x.divide(i));
      if (y.equal(py))
        break;
      py = new dfp(y);
    }

    return y;
  }

  /** computes the arc tangent of the argument 
   *  
   *  Uses the typical taylor series
   *
   *  but may reduce arguments using the following identity
   * tan(x+y) = (tan(x) + tan(y)) / (1 - tan(x)*tan(y))
   *
   * since tan(PI/8) = sqrt(2)-1,
   *
   * atan(x) = atan( (x - sqrt(2) + 1) / (1+x*sqrt(2) - x) + PI/8.0
   */
  public static dfp atan(dfp a)
  {
    dfp x, y, ty;
    boolean recp = false;
    boolean neg = false;
    boolean sub = false;

    ty = SQR2_SPLIT[0].subtract(dfp.one).add(SQR2_SPLIT[1]);

    x = new dfp(a);
    if (x.lessThan(dfp.zero))
    {
      neg = true;
      x = x.negate();
    }

    if (x.greaterThan(dfp.one))
    {
      recp = true; 
      x = dfp.one.divide(x);
    }

    if (x.greaterThan(ty))
    {
      dfp sty[] = new dfp[2];
      dfp xs[] = new dfp[2];
      dfp ds[] = new dfp[2];
      sub = true;

      sty[0] = SQR2_SPLIT[0].subtract(dfp.one);
      sty[1] = SQR2_SPLIT[1];

      xs = split(x);

      ds = splitMult(xs, sty);
      ds[0] = ds[0].add(dfp.one);

      xs[0] = xs[0].subtract(sty[0]);
      xs[1] = xs[1].subtract(sty[1]);

      xs = splitDiv(xs, ds);
      x = xs[0].add(xs[1]);

      //x = x.subtract(ty).divide(dfp.one.add(x.multiply(ty)));
    }

    y = atanInternal(x);

    if (sub)
      y = y.add(PI_SPLIT[0].divide(8)).add(PI_SPLIT[1].divide(8));

    if (recp)
      y = PI_SPLIT[0].divide(2).subtract(y).add(PI_SPLIT[1].divide(2));

    if (neg)
      y = y.negate(); 

    return a.newInstance(y);
  }

  public static dfp asin(dfp a)
  {
    return atan(a.divide(dfp.one.subtract(a.multiply(a)).sqrt()));
  }

  public static dfp acos(dfp a)
  {
    dfp result;
    boolean negative = false;
  
    if (a.lessThan(dfp.zero))
      negative = true;

    a = dfp.copysign(a, dfp.one);  // absolute value

    result = atan(dfp.one.subtract(a.multiply(a)).sqrt().divide(a));
 
    if (negative)
      result = PI.subtract(result);

    return a.newInstance(result);
  }
}
