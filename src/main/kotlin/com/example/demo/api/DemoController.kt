package com.example.demo.api

import com.example.demo.config.SampleProperties
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Demo endpoints")
@SecurityRequirement(name = "basic-auth")
@RequestMapping("/greet")
@RestController
class DemoController(val properties: SampleProperties) {

    @Operation(summary = "Greet with given name")
    @GetMapping("/{name}")
    @ResponseBody
    fun getGreeting(
        @PathVariable("name") name: String
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Hello $name")
    }

    @Operation(summary = "Get configured id")
    @GetMapping("")
    @ResponseBody
    fun getId(): ResponseEntity<String> {
        return ResponseEntity.ok("ID: ${properties.id}")
    }
}
