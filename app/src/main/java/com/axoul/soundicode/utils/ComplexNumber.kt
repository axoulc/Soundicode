package com.axoul.soundicode.utils

import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * `ComplexNumber` is a class which implements complex numbers in Java.
 * It includes basic operations that can be performed on complex numbers such as,
 * addition, subtraction, multiplication, conjugate, modulus and squaring.
 * The data type for Complex Numbers.
 * <br></br><br></br>
 * The features of this library include:<br></br>
 *
 *  * Arithmetic Operations (addition, subtraction, multiplication, division)
 *  * Complex Specific Operations - Conjugate, Inverse, Absolute/Magnitude, Argument/Phase
 *  * Trigonometric Operations - sin, cos, tan, cot, sec, cosec
 *  * Mathematical Functions - exp
 *  * Complex Parsing of type x+yi
 *
 *
 * @author Abdul Fatir
 * @version 1.2
 */
class ComplexNumber {
    /**
     * The real part of `ComplexNumber`
     *
     * @return the real part of the complex number
     */
    /**
     * The real, Re(z), part of the `ComplexNumber`.
     */
    var re: Double
        private set
    /**
     * The imaginary part of `ComplexNumber`
     *
     * @return the imaginary part of the complex number
     */
    /**
     * The imaginary, Im(z), part of the `ComplexNumber`.
     */
    var im: Double
        private set

    /**
     * Constructs a new `ComplexNumber` object with both real and imaginary parts 0 (z = 0 + 0i).
     */
    constructor() {
        re = 0.0
        im = 0.0
    }

    /**
     * Constructs a new `ComplexNumber` object.
     *
     * @param real      the real part, Re(z), of the complex number
     * @param imaginary the imaginary part, Im(z), of the complex number
     */
    constructor(real: Double, imaginary: Double) {
        re = real
        im = imaginary
    }

    /**
     * Adds another `ComplexNumber` to the current complex number.
     *
     * @param z the complex number to be added to the current complex number
     */
    fun add(z: ComplexNumber) {
        set(add(this, z))
    }

    /**
     * Subtracts another `ComplexNumber` from the current complex number.
     *
     * @param z the complex number to be subtracted from the current complex number
     */
    fun subtract(z: ComplexNumber) {
        set(subtract(this, z))
    }

    /**
     * Multiplies another `ComplexNumber` to the current complex number.
     *
     * @param z the complex number to be multiplied to the current complex number
     */
    fun multiply(z: ComplexNumber) {
        set(multiply(this, z))
    }

    /**
     * Divides the current `ComplexNumber` by another `ComplexNumber`.
     *
     * @param z the divisor
     */
    fun divide(z: ComplexNumber) {
        set(divide(this, z))
    }

    /**
     * Sets the value of current complex number to the passed complex number.
     *
     * @param z the complex number
     */
    fun set(z: ComplexNumber) {
        re = z.re
        im = z.im
    }

    /**
     * The complex conjugate of the current complex number.
     *
     * @return a `ComplexNumber` object which is the conjugate of the current complex number
     */
    fun conjugate(): ComplexNumber {
        return ComplexNumber(re, -im)
    }

    /**
     * The modulus, magnitude or the absolute value of current complex number.
     *
     * @return the magnitude or modulus of current complex number
     */
    fun mod(): Double {
        return sqrt(re.pow(2.0) + im.pow(2.0))
    }

    /**
     * The square of the current complex number.
     *
     * @return a `ComplexNumber` which is the square of the current complex number.
     */
    fun square(): ComplexNumber {
        val _real = re * re - im * im
        val _imaginary = 2 * re * im
        return ComplexNumber(_real, _imaginary)
    }

    /**
     * @return the complex number in x + yi format
     */
    override fun toString(): String {
        val re = re.toString() + ""
        var im = ""
        im = if (this.im < 0) this.im.toString() + "i" else "+" + this.im + "i"
        return re + im
    }

    /**
     * The argument/phase of the current complex number.
     *
     * @return arg(z) - the argument of current complex number
     */
    val arg: Double
        get() = atan2(im, re)

    /**
     * Checks if the passed `ComplexNumber` is equal to the current.
     *
     * @param z the complex number to be checked
     * @return true if they are equal, false otherwise
     */
    override fun equals(z: Any?): Boolean {
        if (z !is ComplexNumber) return false
        val a = z
        return re == a.re && im == a.im
    }

    /**
     * The inverse/reciprocal of the complex number.
     *
     * @return the reciprocal of current complex number.
     */
    fun inverse(): ComplexNumber {
        return divide(ComplexNumber(1.0, 0.0), this)
    }

    /**
     * Formats the Complex number as x+yi or r.cis(theta)
     *
     * @param format_id the format ID `ComplexNumber.XY` or `ComplexNumber.RCIS`.
     * @return a string representation of the complex number
     * @throws IllegalArgumentException if the format_id does not match.
     */
    @Throws(IllegalArgumentException::class)
    fun format(format_id: Int): String {
        var out = ""
        out = when (format_id) {
            XY -> toString()
            RCIS -> {
                mod().toString() + " cis(" + arg + ")"
            }
            else -> {
                throw IllegalArgumentException("Unknown Complex Number format.")
            }
        }
        return out
    }

    companion object {
        /**
         * Used in `format(int)` to format the complex number as x+yi
         */
        const val XY = 0

        /**
         * Used in `format(int)` to format the complex number as R.cis(theta), where theta is arg(z)
         */
        const val RCIS = 1

        /**
         * Adds two `ComplexNumber`.
         *
         * @param z1 the first `ComplexNumber`.
         * @param z2 the second `ComplexNumber`.
         * @return the resultant `ComplexNumber` (z1 + z2).
         */
        fun add(z1: ComplexNumber, z2: ComplexNumber): ComplexNumber {
            return ComplexNumber(z1.re + z2.re, z1.im + z2.im)
        }

        /**
         * Subtracts one `ComplexNumber` from another.
         *
         * @param z1 the first `ComplexNumber`.
         * @param z2 the second `ComplexNumber`.
         * @return the resultant `ComplexNumber` (z1 - z2).
         */
        fun subtract(z1: ComplexNumber, z2: ComplexNumber): ComplexNumber {
            return ComplexNumber(z1.re - z2.re, z1.im - z2.im)
        }

        /**
         * Multiplies one `ComplexNumber` to another.
         *
         * @param z1 the first `ComplexNumber`.
         * @param z2 the second `ComplexNumber`.
         * @return the resultant `ComplexNumber` (z1 * z2).
         */
        fun multiply(z1: ComplexNumber, z2: ComplexNumber): ComplexNumber {
            val _real = z1.re * z2.re - z1.im * z2.im
            val _imaginary = z1.re * z2.im + z1.im * z2.re
            return ComplexNumber(_real, _imaginary)
        }

        /**
         * Divides one `ComplexNumber` by another.
         *
         * @param z1 the first `ComplexNumber`.
         * @param z2 the second `ComplexNumber`.
         * @return the resultant `ComplexNumber` (z1 / z2).
         */
        fun divide(z1: ComplexNumber, z2: ComplexNumber): ComplexNumber {
            val output = multiply(z1, z2.conjugate())
            val div = z2.mod().pow(2.0)
            return ComplexNumber(output.re / div, output.im / div)
        }

        /**
         * Calculates the exponential of the `ComplexNumber`
         *
         * @param z The input complex number
         * @return a `ComplexNumber` which is e^(input z)
         */
        fun exp(z: ComplexNumber): ComplexNumber {
            var a = z.re
            var b = z.im
            val r = kotlin.math.exp(a)
            a = r * kotlin.math.cos(b)
            b = r * kotlin.math.sin(b)
            return ComplexNumber(a, b)
        }

        /**
         * Calculates the `ComplexNumber` to the passed integer power.
         *
         * @param z     The input complex number
         * @param power The power.
         * @return a `ComplexNumber` which is (z)^power
         */
        fun pow(z: ComplexNumber, power: Int): ComplexNumber {
            var output = ComplexNumber(z.re, z.im)
            for (i in 1 until power) {
                val _real = output.re * z.re - output.im * z.im
                val _imaginary = output.re * z.im + output.im * z.re
                output = ComplexNumber(_real, _imaginary)
            }
            return output
        }

        /**
         * Calculates the sine of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the sine of z.
         */
        fun sin(z: ComplexNumber): ComplexNumber {
            val x = kotlin.math.exp(z.im)
            val x_inv = 1 / x
            val r = kotlin.math.sin(z.re) * (x + x_inv) / 2
            val i = kotlin.math.cos(z.re) * (x - x_inv) / 2
            return ComplexNumber(r, i)
        }

        /**
         * Calculates the cosine of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the cosine of z.
         */
        fun cos(z: ComplexNumber): ComplexNumber {
            val x = kotlin.math.exp(z.im)
            val x_inv = 1 / x
            val r = kotlin.math.cos(z.re) * (x + x_inv) / 2
            val i = -kotlin.math.sin(z.re) * (x - x_inv) / 2
            return ComplexNumber(r, i)
        }

        /**
         * Calculates the tangent of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the tangent of z.
         */
        fun tan(z: ComplexNumber): ComplexNumber {
            return divide(sin(z), cos(z))
        }

        /**
         * Calculates the co-tangent of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the co-tangent of z.
         */
        fun cot(z: ComplexNumber): ComplexNumber {
            return divide(ComplexNumber(1.0, 0.0), tan(z))
        }

        /**
         * Calculates the secant of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the secant of z.
         */
        fun sec(z: ComplexNumber): ComplexNumber {
            return divide(ComplexNumber(1.0, 0.0), cos(z))
        }

        /**
         * Calculates the co-secant of the `ComplexNumber`
         *
         * @param z the input complex number
         * @return a `ComplexNumber` which is the co-secant of z.
         */
        fun cosec(z: ComplexNumber): ComplexNumber {
            return divide(ComplexNumber(1.0, 0.0), sin(z))
        }

        /**
         * Parses the `String` as a `ComplexNumber` of type x+yi.
         *
         * @param s the input complex number as string
         * @return a ``<<27));
         * break;
         * case 32000:
         * out.write(intToBytes(4<<27));
         * break;
         * case 44100:
         * out.write(intToBytes(5<<27));
         * break;
         * case 48000:
         * out.write(intToBytes(6<<27));
         * break;
         * default:
         * return null;
         * }
         * out.write(intToBytes(0));
         * out.write(intToBytes(0));
         * out.write(intToBytes(numberSamples+((int)((float)sampleRateHz * 0.24))));
         * out.write(intToBytes((15 << 19) + 0x40000));
         * out.write(intToBytes(0x40000000));
         * out.write(intToBytes(0)); //Size
         * for (Map.Entry<FrequencyBand></FrequencyBand>, List<FrequencyPeak>> entry : freqBandToSoundPeaks.entrySet()) {
         * ByteArrayOutputStream peaksArray = new ByteArrayOutputStream();
         * int fftPassNum = 0;
         * FrequencyBand band = entry.getKey();
         * List<FrequencyPeak> peaksList = entry.getValue();
         *
         * for (FrequencyPeak fp : peaksList) {>ComplexNumber which is represented by the string.
        </FrequencyPeak></FrequencyPeak> */
        fun parseComplex(s: String): ComplexNumber? {
            var s = s
            s = s.replace(" ".toRegex(), "")
            var parsed: ComplexNumber? = null
            if (s.contains("+") || s.contains("-") && s.lastIndexOf('-') > 0) {
                var re = ""
                var im = ""
                s = s.replace("i".toRegex(), "")
                s = s.replace("I".toRegex(), "")
                if (s.indexOf('+') > 0) {
                    re = s.substring(0, s.indexOf('+'))
                    im = s.substring(s.indexOf('+') + 1, s.length)
                    parsed = ComplexNumber(re.toDouble(), im.toDouble())
                } else if (s.lastIndexOf('-') > 0) {
                    re = s.substring(0, s.lastIndexOf('-'))
                    im = s.substring(s.lastIndexOf('-') + 1, s.length)
                    parsed = ComplexNumber(re.toDouble(), -im.toDouble())
                }
            } else {
                // Pure imaginary number
                if (s.endsWith("i") || s.endsWith("I")) {
                    s = s.replace("i".toRegex(), "")
                    s = s.replace("I".toRegex(), "")
                    parsed = ComplexNumber(0.0, s.toDouble())
                } else {
                    parsed = ComplexNumber(s.toDouble(), 0.0)
                }
            }
            return parsed
        }
    }
}