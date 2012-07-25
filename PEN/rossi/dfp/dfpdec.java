
package rossi.dfp;

/** Subclass of dfp which hides the radix-10000 artifacts of the superclass.  This
 *  should give outward apperances of being a decimal number with DIGITS*4-3 decimal 
 *  digits.   This class can be subclassed to appear to be an arbitrary number of
 *  decimal digits less than DIGITS*4-3.
 */
public class dfpdec extends dfp
{
  protected final static int maxdigits = DIGITS*4-3;

  public dfpdec()
  {
    super();
  }

  public dfpdec(dfp d)
  {
    super(d);
    round(0);
  }

  public dfpdec(String s)
  {
    super(s);
    round(0);
  }

  public dfp newInstance()
  {
    return new dfpdec();
  }

  public dfp newInstance(dfp d)
  {
    return new dfpdec(d);
  }

  public dfp newInstance(String s)
  {
    return new dfpdec(s);
  }

  /**
   *  Return the number of decimal digits this class is going to 
   *  represent.  Default implementation returns DIGITS*4-3.  Subclasses
   *  can override this to return something less.
   */
  protected int getDecimalDigits()
  {
    return DIGITS*4-3;
  }

  /** round this given the next digit n using the current rounding mode 
   *  returns a flag if an exception occured
   */
  protected int round(int in)
  {
    int r, rh, rl;
    boolean inc=false;
    int n;
    int digits = getDecimalDigits();
    int lsbshift;
    int lsbthreshold = 1000;
    int lsb;
    int cmaxdigits = DIGITS*4;
    int msb = mant[DIGITS-1];
    int lsd = 0;  // position of least sig radix 10k digit
    int discarded = 0; 

    if (msb == 0) // special case -- this == zero
      return 0;

    while (lsbthreshold > msb)
    {
      lsbthreshold /= 10;
      cmaxdigits --;
    }


    lsbshift = cmaxdigits - digits;
    lsd = lsbshift / 4;

    lsbthreshold = 1;
    for (int i=0;i<(lsbshift%4); i++)
      lsbthreshold *= 10;

    lsb = mant[lsd];

    //System.out.println("LSBShift = "+lsbshift);
    //System.out.println("LSBThreshold = "+lsbthreshold);

    if (lsbthreshold <= 1 && digits == maxdigits)
      return super.round(in);

    discarded |= in;  // not looking at this after this point

    if (lsbthreshold == 1)  // look to the next digit for rounding
    {
      n = (mant[lsd-1] / 1000) % 10;
      mant[lsd-1] %= 1000;
      discarded |= mant[lsd-1];
    }
    else
    {
      n = (lsb * 10 / lsbthreshold) % 10;
      discarded |= (lsb % (lsbthreshold/10));
    }
    //System.out.println("discardedA = "+discarded);

    for (int i=0; i<lsd; i++)
    {
      discarded |= mant[i];    // need to know if thre are any discarded bits
      mant[i] = 0;
    }

    //System.out.println("N = "+n);
    //System.out.println("discardedB = "+discarded);
    //System.out.println("oddeven = "+(lsb/lsbthreshold));

    mant[lsd] = lsb / lsbthreshold * lsbthreshold;

    switch (rMode)
    {
      case ROUND_DOWN:
        inc = false;
        break;

      case ROUND_UP:
        inc = (n!=0 || discarded != 0);       // round up if n!=0
        break;

      case ROUND_HALF_UP:
        inc = (n >= 5);  // round half up
        break;

      case ROUND_HALF_DOWN:
        inc = (n > 5);  // round half down
        break;

      case ROUND_HALF_EVEN:
        inc = (n > 5 || (n == 5 && discarded != 0) || (n == 5 && discarded == 0 && ((lsb/lsbthreshold)&1)==1));  // round half-even
        break;

      case ROUND_HALF_ODD:
        inc = (n > 5 || (n == 5 && discarded != 0) || (n == 5 && discarded == 0 && ((lsb/lsbthreshold)&1)==0));  // round half-odd
        break;

      case ROUND_CEIL:
        inc = (sign == 1 && (n != 0 || discarded != 0));  // round ceil
        break;

      case ROUND_FLOOR:
        inc = (sign == -1 && (n != 0 || discarded !=0));  // round floor
        break;
    }

    if (inc)  // increment if necessary 
    {
      rh = lsbthreshold;
      for (int i=lsd; i<DIGITS; i++)
      {
        r = mant[i] + rh;
        rh = r / radix;
        rl = r % radix;
        mant[i] = rl;
      }

      if (rh != 0)
      {
        shiftRight();
        mant[DIGITS-1]=rh;
      }
    }

    /* Check for exceptional cases and raise signals if necessary */
    if (exp < minExp)  // Gradual Underflow
    {
      ieeeFlags |= FLAG_UNDERFLOW;
      return FLAG_UNDERFLOW;
    }

    if (exp > maxExp)  // Overflow
    {
      ieeeFlags |= FLAG_OVERFLOW;
      return FLAG_OVERFLOW;
    }

    if (n != 0 || discarded != 0)  // Inexact
    {
      ieeeFlags |= FLAG_INEXACT;
      return FLAG_INEXACT;
    }
    return 0;
  }

  /** Returns the next number greater than this one in the direction
   *  of x.  If this==x then simply returns this. */

  public dfp nextAfter(dfp x)
  {
    boolean up = false;
    dfp result, inc;

    // if this is greater than x
    if (this.lessThan(x))
      up = true;

    if (compare(this, x) == 0)
      return newInstance(x);
 
    if (lessThan(zero))
      up = !up;

    if (up)
    {
      inc = power10(log10() - getDecimalDigits() + 1);
      inc = copysign(inc, this);

      if (this.equal(zero))
        inc = power10K(minExp-DIGITS-1);

      if (inc.equal(zero))
        result = copysign(newInstance(zero), this);
      else
        result = add(inc);
    }
    else
    {
      inc = power10(log10());
      inc = copysign(inc, this);

      if (this.equal(inc))
        inc = inc.divide(power10(getDecimalDigits()));
      else
        inc = inc.divide(power10(getDecimalDigits() - 1));

      if (this.equal(zero))
        inc = power10K(minExp-DIGITS-1);

      if (inc.equal(zero))
        result = copysign(newInstance(zero), this);
      else
        result = subtract(inc);
    }
    if (result.classify() == INFINITE && this.classify() != INFINITE)
    {
      ieeeFlags |= FLAG_INEXACT;
      result = dotrap(FLAG_INEXACT, "nextAfter", x, result);
    }
  
    if (result.equal(zero) && this.equal(zero) == false)
    {
      ieeeFlags |= FLAG_INEXACT;
      result = dotrap(FLAG_INEXACT, "nextAfter", x, result);
    }

    return result;
  }
}
