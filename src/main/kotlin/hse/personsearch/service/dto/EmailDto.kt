package hse.personsearch.service.dto

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class EmailDto {

    @Email
    @NotEmpty
    var email: String? = null

    @NotEmpty
    var requestUuid: UUID? = null
}