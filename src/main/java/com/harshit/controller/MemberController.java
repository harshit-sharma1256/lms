package com.harshit.controller;

import com.harshit.entity.Member;
import com.harshit.service.MemberService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;

import java.util.Optional;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/members")
@Tag(name = "Member Management", description = "Operations related to library members")  // Swagger Tag for grouping
public class MemberController {

    @Inject private MemberService memberService;

    @Operation(summary = "Health Check", description = "Check if the service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @Get(value = "/health", produces = APPLICATION_JSON)
    public String health() {
        return "OK";
    }

    @Operation(summary = "Get all Members", description = "Retrieve all members from the library")
    @ApiResponse(responseCode = "200", description = "All Members retrieved successfully")
    @Get(value = "/getAll")
    public Iterable<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @Operation(summary = "Get a Member by Name", description = "Retrieve a specific member by their name")
    @ApiResponse(
            responseCode = "200",
            description = "Member found",
            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Member.class))
    )
    @ApiResponse(responseCode = "404", description = "Member not found")
    @Get(value = "/{name}")
    public HttpResponse<Member> getSpecificMember(@PathVariable("name") String name) {
        Optional<Member> member = memberService.getMemberByName(name);
        return member.isPresent() ? HttpResponse.ok(member.get()) : HttpResponse.notFound();
    }

    @Operation(summary = "Add a New Member", description = "Add a new member to the library")
    @ApiResponse(responseCode = "201", description = "Member added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @Post("/add")
    public HttpResponse<String> addAnotherMember(@Body Member member) {
        return memberService.saveMember(member);
    }

    @Operation(summary = "Update a Member", description = "Update an existing member or conditionally add a new one")
    @ApiResponse(responseCode = "200", description = "Member updated successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @Put("/update/{name}")
    public HttpResponse<String> updateMember(@PathVariable("name") String name, @Body Member member, @QueryValue(defaultValue = "false") boolean confirm) {
        return memberService.updateMember(name, member, confirm);
    }

    @Operation(summary = "Delete a Member", description = "Delete a member by their name")
    @ApiResponse(responseCode = "200", description = "Member deleted successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @Delete("/del/{name}")
    public HttpResponse<String> deleteMember(@PathVariable("name") String name) {
        try {
            memberService.deleteMember(name);
            return HttpResponse.ok("!!! Member deleted successfully !!!");
        } catch (RuntimeException ex) {
            return HttpResponse.notFound("!!! The member you are trying to delete does not exist in the DB. !!!");
        }
    }
}
