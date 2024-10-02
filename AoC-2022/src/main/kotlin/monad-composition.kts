import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Writer<A>(val value: A, val log: String) {
    // For convenience
    fun <B> map(f: (A) -> B): Writer<B> = Writer(f(value), log)

    // This is required for a monad, it's part of the definition.
    // This is called 'bind' or 'join' in the FP/category world
    fun <B> flatMap(f: (A) -> Writer<B>): Writer<B> {
        val result = f(value)
        return Writer(result.value, log + "\n" + result.log)
    }
}

class IO<out A>(private val supply: suspend () -> A) {
    fun <B> map(f: (A) -> B): IO<B> = IO { f(supply()) }

    fun <B> flatMap(f: (A) -> IO<B>): IO<B> = IO { f(supply()).supply() }

    suspend fun get() = supply()

    companion object {
        fun <A> io(f: suspend () -> A): IO<A> = IO(f)
    }
}

sealed class Either<out L, out R> {
    data class Left<L>(val value: L) : Either<L, Nothing>()
    data class Right<R>(val value: R) : Either<Nothing, R>()

    fun <A> fold(ifLeft: (L) -> A, ifRight: (R) -> A): A = when (this) {
        is Left -> ifLeft(value)
        is Right -> ifRight(value)
    }
}

data class RawImageData(val data: String, val prompt: String)
data class OverlordsImage(val data: String, val hasOddAppendages: Boolean)
object Error : Throwable("An error occurred during image processing.")

fun topSecretImageAnalysis(rawImage: RawImageData) = OverlordsImage(data = rawImage.data, hasOddAppendages = true)

fun generateImage(prompt: String): Writer<IO<RawImageData>> {
    //val imageGeneratorUrl = "https://iamgoingtoberich.ai.com/api/v0.00000001/generate?query=$prompt"
    return Writer(
        IO.io {
            // val response = client.get(imageGeneratorUrl)
            RawImageData("fnwoPik2KjFSWV9pP1EnejFmVygjPTolVg==", prompt)
        },
        "Generating image for the following prompt: $prompt"
    )
}

fun processRawImage(rawImage: RawImageData): Writer<IO<OverlordsImage>> = Writer(
    IO.io { topSecretImageAnalysis(rawImage) },
    "Analyzing raw image: ${rawImage.data} for prompt: ${rawImage.prompt}"
)

fun OverlordsImage.toEither() = when (hasOddAppendages) {
    true -> Either.Left(Error)
    else -> Either.Right(this)
}

fun makeDecision(image: OverlordsImage): Writer<String> {
    val decision = image.toEither().fold(
        ifLeft = { "We might have a chance!" },
        ifRight = { "Success!" }
    )

    // we have some decisions to make
    return Writer(
        decision,
        decision
    )
}

// (String) -> Writer<IO<RawImageData>>
val decision = generateImage("A person going higher holding a microphone").flatMap { result ->
    // (RawImageData) -> Writer<IO<OverlordsImage>>
    processRawImage(runBlocking { result.get() }).flatMap { analysis ->
        // (OverlordsImage) -> Writer<String>
        makeDecision(runBlocking { analysis.get() })
    }
}

runBlocking { IO.io { println(decision.log) }.get() }

fun sum(x: Int, y: Int): Writer<Int> = Writer(x + y, "Added $x to $y.")

fun product(x: Int, y: Int): Writer<Int> = Writer(x * y, "Multiplied $x by $y.")

sum(3, 4)