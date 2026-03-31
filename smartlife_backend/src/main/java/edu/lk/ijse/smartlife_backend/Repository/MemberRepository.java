package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);
    List<Member> findAllByAdmin_UserId(String userId);

    @Query(value = "SELECT member_id FROM member ORDER BY member_id DESC LIMIT 1", nativeQuery = true)
    String findLastMemberId();

    @Query("SELECT m FROM Member m WHERE m.admin.userId = :adminId")
    List<Member> findActiveLocationsByAdmin(@Param("adminId") String adminId);
}