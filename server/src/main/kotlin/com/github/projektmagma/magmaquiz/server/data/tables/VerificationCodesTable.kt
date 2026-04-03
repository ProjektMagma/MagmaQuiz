package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object VerificationCodesTable : ExtUUIDTable("verification_codes", "verification_codes_id") {
    val code = varchar("code", 255).nullable()
    val codeOwner = reference("code_owner_id", UsersTable)
}