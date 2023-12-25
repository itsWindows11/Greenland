package dev.itswin11.greenland.models

import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

data class UserDid(val did: Did) : UserReference

data class UserHandle(val handle: Handle) : UserReference

sealed interface UserReference