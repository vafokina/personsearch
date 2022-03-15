package hse.personsearch.web.rest

import hse.personsearch.domain.Constants.AUTO_EXPIRE_QUEUE_MILLIS
import hse.personsearch.domain.Constants.REQUEST_QUEUE_NAME
import hse.personsearch.service.ImageUploadService
import hse.personsearch.service.RabbitQueueService
import hse.personsearch.service.dto.EmailDto
import hse.personsearch.service.dto.MQRequestDto
import hse.personsearch.service.dto.MQResponseDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Api(description = "Endpoints for finding web pages related to the person in the photo")
@RestController
@RequestMapping("/api/search")
class SearchResource {

    @Autowired
    private lateinit var rabbitQueueService: RabbitQueueService

    @Autowired
    private lateinit var imageUploadService: ImageUploadService

    //@Async
    @ApiOperation(
        value = "Send a request to search for web pages related to the person in the photo",
        response = String::class
    )
    @PostMapping()
    fun search(@NotNull @RequestBody file: MultipartFile): UUID {
        val uuid = UUID.randomUUID()

        val imageUrl = imageUploadService.upload(file, null, null)

        val request = MQRequestDto().apply {
            this.imageUrl = imageUrl
            this.uuid = uuid
        }

        rabbitQueueService.createQueue(uuid.toString(), AUTO_EXPIRE_QUEUE_MILLIS)
        rabbitQueueService.send(REQUEST_QUEUE_NAME, request)

        return uuid
    }

    @ApiOperation(
        value = "Get an id of a report with search results by the request uuid (waiting for a response from the queue)",
        response = String::class
    )
    @GetMapping("/{uuid}")
    fun getSearchResult(
        @ApiParam("the request uuid", example = "d54ffc8f-a3cd-4078-a68c-6f5df63e167a", required = true)
        @NotNull @PathVariable uuid: UUID
    ): Long {
        val response = rabbitQueueService.receive<MQResponseDto>(uuid.toString(), ParameterizedTypeReference.forType(MQResponseDto::class.java))

        if (!response.errorCode.isNullOrEmpty()) {
            // TODO implement!!!!!!!
            // как лучше кидать ошибку или передавать в ДТО
        }

        //email
        // TODO implement!!!!!!!

        return response.reportId!!
    }

    @ApiOperation(
        value = "Get the number of requests in the queue",
        response = Int::class
    )
    @GetMapping("/request-count")
    fun getRequestCount(): Int {
        // TODO implement!!!!!!!

        //log.debug("REST request to check if the current user is authenticated")
        return 5
    }

    @ApiOperation(
        value = "Send a user email to notify the user of search results when the results are ready"
    )
    @PostMapping("/email")
    fun notifyByEmail(@Valid @RequestBody emailDto: EmailDto) {
        // TODO implement!!!!!!!
    }

    @PostMapping("/test-send/{uuid}")
    fun test1(@NotNull @PathVariable uuid: UUID, response: MQResponseDto) {
        rabbitQueueService.send(uuid.toString(), response)
    }
}