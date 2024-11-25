package com.harshit.service;

import com.harshit.repository.MemberRepository;
import com.harshit.entity.Member;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.Optional;

@Singleton
public class MemberService {

    @Inject private MemberRepository memberRepository;

    // Retrieve a list of all members
    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Read operation by ID
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    // Read operation by name
    public Optional<Member> getMemberByName(String name) {
        return memberRepository.findByName(name);
    }

    // Create operation
    @Transactional
    public HttpResponse<String> saveMember(Member member) {

        if (member.getName() == null || member.getName().isEmpty()) {
            return HttpResponse.badRequest("!!! Member name is required !!!");
        }
        Optional<Member> existingMemberOpt = memberRepository.findByName(member.getName());

        if (existingMemberOpt.isPresent()) {
            return HttpResponse.badRequest("This member already exists. Please use the update operation.");
        } else {
            Member newMember = new Member();
            newMember.setName(member.getName());
            newMember.setEmail(member.getEmail() != null ? member.getEmail() : "No Email");

//            newMember.setBooksIssued(member.getBooksIssued() > 0 ? member.getBooksIssued() : 0); // Set to 0 if not provided

            memberRepository.save(newMember);
            return HttpResponse.ok("Member added successfully!");
        }
    }

    // Update operation
    @Transactional
    public HttpResponse<String> updateMember(String name, Member member, boolean confirm) {
        Optional<Member> existingMemberOpt = memberRepository.findByName(name);

        if (existingMemberOpt.isPresent()) {
            Member existingMember = existingMemberOpt.get();

            // Update only the fields provided in the request body
            if (member.getName() != null) {
                existingMember.setName(member.getName());
            }
            if (member.getEmail() != null) {
                existingMember.setEmail(member.getEmail());
            }
//            if (member.getBooksIssued() > 0) { // Only update if booksIssued is a positive number
//                existingMember.setBooksIssued(member.getBooksIssued());
//            }

            memberRepository.update(existingMember);
            return HttpResponse.ok("Member updated successfully!");
        } else {
            if (confirm) {
                if (member.getName() == null || member.getName().isEmpty()) {
                    return HttpResponse.badRequest("!!! Member name is required !!!");
                }
                Member newMember = new Member();
                newMember.setName(member.getName());
                newMember.setEmail(member.getEmail() != null ? member.getEmail() : "No Email");

//                if (member.getBooksIssued() > 0) {
//                    newMember.setBooksIssued(member.getBooksIssued());
//                }

                memberRepository.save(newMember);
                return HttpResponse.ok("Member didn't exist, but has now been added successfully!");
            } else {
                return HttpResponse.badRequest("This member doesn't exist. Do you want to add a new member with these values? Set `confirm=true` in the query parameter to add it.");
            }
        }
    }

    // Delete operation
    @Transactional
    public void deleteMember(String name) {
        Optional<Member> existingMemberOpt = memberRepository.findByName(name);
        if (existingMemberOpt.isPresent()) {
            memberRepository.deleteByName(name);
        } else {
            throw new RuntimeException();
        }
    }
}
