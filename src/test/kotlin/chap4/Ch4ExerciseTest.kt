package chap4

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

object Factorial {
    private lateinit var fact: (Int) -> Int;
    init {
        fact = { if (it == 1) it else it * fact(it - 1) }
    }

    val factorial = fact
}

class Ch4ExerciseTest {

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            fun inc(n: Int): Int = n + 1
            fun dec(n: Int): Int = n - 1

            tailrec fun add(a: Int, b:Int): Int = if (b <= 0) a else add(inc(a), dec(b))

            assertEquals(add(5, 10), 15)
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            fun factorialRecursive(n: Int): Int = if (n == 0) 1 else n * factorialRecursive(n - 1)

            fun factorialTailRecursive(n: Int): Int {
                tailrec fun f(result: Int, n: Int): Int = if (n == 1) result else f(result * n, n - 1)

                return f(1, n)
            }

            val factorialTailRecursiveLambda: (Int) -> Int = { n ->
                tailrec fun f(result: Int, n: Int): Int = if (n == 1) result else f(result * n, n - 1)
                f(1, n)
            }

            assertEquals(factorialRecursive(5), 120)
            assertEquals(factorialTailRecursive(5), 120)
            assertEquals(factorialTailRecursiveLambda(5), 120)
            assertEquals(Factorial.factorial(5), 120)
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            fun fibonacciRecursive(n: Int): Int = if (n <= 1) 1 else fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)

            fun fibonacciIterative(n: Int): BigInteger {
                if (n <= 1) {
                    return BigInteger.ONE
                }
                var iter = n;
                var val1 = BigInteger.ONE
                var val2 = BigInteger.ONE

                while (iter > 1) {
                    var temp = val1
                    val1 = val2
                    val2 += temp
                    iter--
                }

                return val2
            }

            fun fibonacci(n: Int): BigInteger {
                tailrec fun fibonacci(currentStep: Int, val1: BigInteger, val2: BigInteger): BigInteger =
                    if (currentStep == n) val2
                    else fibonacci(currentStep + 1, val2, val1 + val2)

                return fibonacci(0, BigInteger.ZERO, BigInteger.ONE)
            }

            fun fib(x: Int): BigInteger {
                tailrec fun fib(val1: BigInteger, val2: BigInteger, x: BigInteger): BigInteger =
                    when {
                        (x == BigInteger.ZERO) -> BigInteger.ONE
                        (x == BigInteger.ONE) -> val1 + val2
                        else -> fib(val2, val1 + val2, x - BigInteger.ONE)
                    }

                return fib(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(x.toLong()))
            }

            assertEquals(fibonacciRecursive(0), 1)
            assertEquals(fibonacciRecursive(1), 1)
            assertEquals(fibonacciRecursive(2), 2)
            assertEquals(fibonacciRecursive(3), 3)
            assertEquals(fibonacciRecursive(4), 5)
            assertEquals(fibonacciRecursive(5), 8)
            // assertEquals(fibonacciRecursive(100), 8) // too slow

            assertEquals(fibonacciIterative(0), BigInteger.ONE)
            assertEquals(fibonacciIterative(1), BigInteger.ONE)
            assertEquals(fibonacciIterative(2), BigInteger.TWO)
            assertEquals(fibonacciIterative(3), BigInteger.valueOf(3))
            assertEquals(fibonacciIterative(4), BigInteger.valueOf(5))
            assertEquals(fibonacciIterative(5), BigInteger.valueOf(8))
            assertEquals(fibonacciIterative(100).toString(10), "573147844013817084101")
            assertEquals(fibonacciIterative(10_000).toString(10), "54438373113565281338734260993750380135389184554695967026247715841208582865622349017083051547938960541173822675978026317384359584751116241439174702642959169925586334117906063048089793531476108466259072759367899150677960088306597966641965824937721800381441158841042480997984696487375337180028163763317781927941101369262750979509800713596718023814710669912644214775254478587674568963808002962265133111359929762726679441400101575800043510777465935805362502461707918059226414679005690752321895868142367849593880756423483754386342639635970733756260098962462668746112041739819404875062443709868654315626847186195620146126642232711815040367018825205314845875817193533529827837800351902529239517836689467661917953884712441028463935449484614450778762529520961887597272889220768537396475869543159172434537193611263743926337313005896167248051737986306368115003088396749587102619524631352447499505204198305187168321623283859794627245919771454628218399695789223798912199431775469705216131081096559950638297261253848242007897109054754028438149611930465061866170122983288964352733750792786069444761853525144421077928045979904561298129423809156055033032338919609162236698759922782923191896688017718575555520994653320128446502371153715141749290913104897203455577507196645425232862022019506091483585223882711016708433051169942115775151255510251655931888164048344129557038825477521111577395780115868397072602565614824956460538700280331311861485399805397031555727529693399586079850381581446276433858828529535803424850845426446471681531001533180479567436396815653326152509571127480411928196022148849148284389124178520174507305538928717857923509417743383331506898239354421988805429332440371194867215543576548565499134519271098919802665184564927827827212957649240235507595558205647569365394873317659000206373126570643509709482649710038733517477713403319028105575667931789470024118803094604034362953471997461392274791549730356412633074230824051999996101549784667340458326852960388301120765629245998136251652347093963049734046445106365304163630823669242257761468288461791843224793434406079917883360676846711185597501")

            assertEquals(fibonacci(0), BigInteger.ONE)
            assertEquals(fibonacci(1), BigInteger.ONE)
            assertEquals(fibonacci(2), BigInteger.TWO)
            assertEquals(fibonacci(3), BigInteger.valueOf(3L))
            assertEquals(fibonacci(4), BigInteger.valueOf(5L))
            assertEquals(fibonacci(5), BigInteger.valueOf(8L))
            assertEquals(fibonacci(47), BigInteger.valueOf(4807526976L))
            assertEquals(fibonacci(100).toString(10), "573147844013817084101")
            assertEquals(fibonacci(10_000).toString(10), "54438373113565281338734260993750380135389184554695967026247715841208582865622349017083051547938960541173822675978026317384359584751116241439174702642959169925586334117906063048089793531476108466259072759367899150677960088306597966641965824937721800381441158841042480997984696487375337180028163763317781927941101369262750979509800713596718023814710669912644214775254478587674568963808002962265133111359929762726679441400101575800043510777465935805362502461707918059226414679005690752321895868142367849593880756423483754386342639635970733756260098962462668746112041739819404875062443709868654315626847186195620146126642232711815040367018825205314845875817193533529827837800351902529239517836689467661917953884712441028463935449484614450778762529520961887597272889220768537396475869543159172434537193611263743926337313005896167248051737986306368115003088396749587102619524631352447499505204198305187168321623283859794627245919771454628218399695789223798912199431775469705216131081096559950638297261253848242007897109054754028438149611930465061866170122983288964352733750792786069444761853525144421077928045979904561298129423809156055033032338919609162236698759922782923191896688017718575555520994653320128446502371153715141749290913104897203455577507196645425232862022019506091483585223882711016708433051169942115775151255510251655931888164048344129557038825477521111577395780115868397072602565614824956460538700280331311861485399805397031555727529693399586079850381581446276433858828529535803424850845426446471681531001533180479567436396815653326152509571127480411928196022148849148284389124178520174507305538928717857923509417743383331506898239354421988805429332440371194867215543576548565499134519271098919802665184564927827827212957649240235507595558205647569365394873317659000206373126570643509709482649710038733517477713403319028105575667931789470024118803094604034362953471997461392274791549730356412633074230824051999996101549784667340458326852960388301120765629245998136251652347093963049734046445106365304163630823669242257761468288461791843224793434406079917883360676846711185597501")

            assertEquals(fib(10_000).toString(10), "54438373113565281338734260993750380135389184554695967026247715841208582865622349017083051547938960541173822675978026317384359584751116241439174702642959169925586334117906063048089793531476108466259072759367899150677960088306597966641965824937721800381441158841042480997984696487375337180028163763317781927941101369262750979509800713596718023814710669912644214775254478587674568963808002962265133111359929762726679441400101575800043510777465935805362502461707918059226414679005690752321895868142367849593880756423483754386342639635970733756260098962462668746112041739819404875062443709868654315626847186195620146126642232711815040367018825205314845875817193533529827837800351902529239517836689467661917953884712441028463935449484614450778762529520961887597272889220768537396475869543159172434537193611263743926337313005896167248051737986306368115003088396749587102619524631352447499505204198305187168321623283859794627245919771454628218399695789223798912199431775469705216131081096559950638297261253848242007897109054754028438149611930465061866170122983288964352733750792786069444761853525144421077928045979904561298129423809156055033032338919609162236698759922782923191896688017718575555520994653320128446502371153715141749290913104897203455577507196645425232862022019506091483585223882711016708433051169942115775151255510251655931888164048344129557038825477521111577395780115868397072602565614824956460538700280331311861485399805397031555727529693399586079850381581446276433858828529535803424850845426446471681531001533180479567436396815653326152509571127480411928196022148849148284389124178520174507305538928717857923509417743383331506898239354421988805429332440371194867215543576548565499134519271098919802665184564927827827212957649240235507595558205647569365394873317659000206373126570643509709482649710038733517477713403319028105575667931789470024118803094604034362953471997461392274791549730356412633074230824051999996101549784667340458326852960388301120765629245998136251652347093963049734046445106365304163630823669242257761468288461791843224793434406079917883360676846711185597501")
        }
    }
}