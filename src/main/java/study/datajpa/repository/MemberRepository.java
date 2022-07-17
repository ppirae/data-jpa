package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    //@Query(name = "Member.findByUsername")  없어도 잘 동작함
    List<Member> findByUsername(@Param("username") String username);

    //이 기능을 실무에서 많이 사용함
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);   //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional'

    Page<Member> findByAge(int age, Pageable pageable);

    //Modifying을 통한 벌크 변경
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //페치 조인 사용
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //EntityGraph만 사용
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //EntityGraph + Query 같이 사용
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //NamedEntityGraph 사용
    @EntityGraph(attributePaths = {"team"})
    //@EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //힌트 -> 변경안됨 (변경 감지를 안함)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //락 -> for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
