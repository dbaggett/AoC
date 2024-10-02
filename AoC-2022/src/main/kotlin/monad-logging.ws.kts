import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//typealias Writer<T> = Pair<T, String>

fun add(a: Int, b: Int): Pair<Int, String> {
    val result = a + b
    // This does the same thing every time without side effects
    // and captures the essence of the original, impure function
    return Pair(result, "[add] $a, $b => $result")
}

class Writer<A>(val value: A, val log: String) {
    // For convenience
    fun <B> map(f: (A) -> B): Writer<B> = Writer(f(value), log)

    // This is required for a monad, it's part of the definition.
    // This is called 'bind' or 'join' in the FP/category world
    fun <B> flatMap(f: (A) -> Writer<B>): Writer<B> {
        val result = f(value)
        return Writer(result.value, log + " " + result.log)
    }
}

fun sum(x: Int, y: Int): Writer<Int> = Writer(x + y, "Added $x to $y.")

fun product(x: Int, y: Int): Writer<Int> = Writer(x * y, "Multiplied $x by $y.")

class IO<out A>(private val supply: suspend () -> A) {
    fun <B> map(f: (A) -> B): IO<B> = IO { f(supply()) }

    fun <B> flatMap(f: (A) -> IO<B>): IO<B> = IO { f(supply()).supply() }

    companion object {
        fun <A> io(f: suspend () -> A): IO<A> = IO(f)
    }
}

// Monads allow us to have full control of computations and (side) effects
val total = sum(3, 3).flatMap { sumResult ->
    product(sumResult, 7).flatMap { productResult ->
        sum(productResult, SumMonoid().unit())
    }
}

val userId = IO.io {
    // Simulate fetching user ID from some IO medium
    delay(2000)
    "1234"
}

fun logData(userId: String, log: String): IO<Unit> = IO.io {
    // Simulate sending log to some output
    delay(500)
    println("User $userId performed the following computations: $log")
}

val result = userId.flatMap { id ->
    logData(id, total.log).map {  }
}

runBlocking {
    // User 1234 performed the following computations: Added 3 to 3. Multiplied 6 by 7. Added 42 to 0.
    result
}

// must have neutral element and a binary operation
// the binary operation must be associative for values of T
interface Monoid<T> {
    fun unit(): T
    fun combine(a: T, b: T): T
}

class StringMonoid : Monoid<String> {
    // "hello" + unit() == unit() + "hello"
    // monoids are not required to be commutative
    override fun unit() = ""

    // ("h" + "e" + "l") + ("l" + "o") == "h" + ("e" + "l" + "l" + "o")
    override fun combine(a: String, b: String) = a + b
}

class SumMonoid : Monoid<Int> {
    // 1 + unit() == unit() + 1
    // monoids are not required to be commutative
    override fun unit() = 0

    // (1 + 2) + 3 == 1 + (2 + 3)
    override fun combine(a: Int, b: Int) = a + b
}

/**class Adder {
    fun sum(numbers: List<Int>): Writer<Int> {
        numbers.fold(Pair("", "")){ acc, number -> add(acc, number)}
    }
}*/