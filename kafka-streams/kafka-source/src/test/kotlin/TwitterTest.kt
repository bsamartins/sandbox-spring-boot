import org.apache.commons.io.IOUtils
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.InputStream
import java.io.SequenceInputStream
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.CountDownLatch

private const val SAMPLE_STREAM_URL: String = "https://stream.twitter.com/1.1/statuses/sample.json"
private const val HOME_TIMELINE_URL: String = "https://api.twitter.com/1.1/statuses/home_timeline.json"
private const val OAUTH_URL: String = "https://api.twitter.com/oauth2/token"
//private const val OAUTH_URL: String = "http://localhost:7666/oauth2/token"

private const val CONSUMER_KEY: String = ""
private const val CONSUMER_SECRET: String = ""

fun main(args: Array<String>) {
    val lock: CountDownLatch = CountDownLatch(1)
    val token: Token = requestToken().block()
    val t: String? = Base64.getEncoder().encodeToString("${token.token_type} ${token.access_token}".toByteArray())
    WebClient.builder()
            .baseUrl(HOME_TIMELINE_URL)
            .defaultHeader("Authorization", "Basic ${t}")
            .build()
            .get()
            .exchange()
            .flatMap { clientResponse ->
                if(clientResponse.statusCode().is2xxSuccessful) {
                    clientResponse.body(BodyExtractors.toDataBuffers())
                            .collect(::InputStreamCollector, { t, dataBuffer -> t.collectInputStream(dataBuffer.asInputStream()) })
                } else {
                    Mono.error(Exception("Response Error: ${clientResponse.statusCode()}"))
                }
            }.map { inputStream -> IOUtils.toString(inputStream.getInputStream(), "utf-8") }
            .doOnError { lock.countDown() }
            .subscribe { data ->
                println("*********************")
                println(data)
                println("*********************")
                lock.countDown()
            }
    lock.await()
}

// Encodes the consumer key and secret to create the basic authorization key

fun encodeKeys(consumerKey: String, consumerSecret: String): String {
    val encodedConsumerKey: String = URLEncoder.encode(consumerKey, "UTF-8")
    val encodedConsumerSecret: String = URLEncoder.encode(consumerSecret, "UTF-8")

    val fullKey: String = encodedConsumerKey + ":" + encodedConsumerSecret
    val encodedBytes: ByteArray = Base64.getEncoder().encode(fullKey.toByteArray())
    return String(encodedBytes)
}


fun requestToken(): Mono<Token> {
    val authorizationHeader: String = encodeKeys(CONSUMER_KEY, CONSUMER_SECRET)
    return WebClient.builder()
            .baseUrl(OAUTH_URL)
            .defaultHeader("Authorization", "Basic $authorizationHeader")
            .build()
            .post()
            .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
            .exchange()
            .flatMap { response ->
                if(response.statusCode().isError) {
                    response.bodyToMono(String::class.java)
                            .flatMap { text ->
                                throw Exception("Error retrieving credentials [${response.statusCode()}]: $text ")
                            }
                } else {
                    response.bodyToMono(Token::class.java)
                }
            }
}

class InputStreamCollector {
    private lateinit var inputStream: InputStream

    fun collectInputStream(inputStream: InputStream) {
        if (this.inputStream == null) {
            this.inputStream = inputStream;
        }

        this.inputStream = SequenceInputStream(this.inputStream, inputStream)
    }

    fun getInputStream(): InputStream {
        return this.inputStream
    }
}

class Token {
    lateinit var token_type: String
    lateinit var access_token: String
}