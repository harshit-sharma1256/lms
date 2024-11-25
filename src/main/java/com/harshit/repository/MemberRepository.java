package com.harshit.repository;

import com.harshit.entity.Member;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;


import java.util.Optional;

@Repository
public interface MemberRepository  extends JpaRepository<Member,Long> {
    Optional<Member> findByName(String name);
    void deleteByName(String name);
}
