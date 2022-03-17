package hse.personsearch.domain

enum class Error(errorCode: String) {
    NO_PERSON_ERROR("no_person_error"),
    MORE_THAN_ONE_PERSON_ERROR("more_than_one_person_error"),
    UNEXPECTED_SERVER_ERROR("unexpected_server_error")
}