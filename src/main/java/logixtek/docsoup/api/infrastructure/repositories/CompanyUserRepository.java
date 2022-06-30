package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.models.CompanyUser;
import logixtek.docsoup.api.infrastructure.models.TeammateStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyUserRepository extends JpaRepository<CompanyUserEntity, Long> {

    Optional<CompanyUserEntity> findFirstByEmailAndInvitationStatusAndAccountIdIsNull(String email, Integer invitationStatus);

    Optional<CompanyUserEntity> findFirstByAccountIdAndCompanyId(String accountId, UUID companyId);

    @Query("select cu from CompanyUserEntity  cu where cu.companyId = :companyId and cu.member_type = :member_type and cu.accountId is not null and cu.accountId <> :accountId"
    )
    Optional<CompanyUserEntity> findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNullAndAccountIdNotEqual(UUID companyId, @Param("member_type") Integer member_type, String accountId);

    @Query("select cu.id as id, cu.accountId as userId , concat(ac.firstName,' ',ac.lastName) as fullName, cu.email as email,cu.member_type as member, cu.status as status, cu.role as role from CompanyUserEntity  cu "+
            "left join AccountEntity ac "+
            "on cu.accountId = ac.id "+
            "where cu.companyId = :companyId"
          )
    Optional<Collection<CompanyUser>> findAllCompanyUserByCompanyId(@Param("companyId") UUID companyId);

    Optional<Collection<CompanyUserEntity>> findAllByEmailInAndCompanyId(List<String> emails, UUID companyId);

    Optional<Collection<CompanyUserEntity>> findAllCompanyUserByEmailAndInvitationStatusAndAccountIdIsNull(String email, Integer invitationStatus);

    Optional<CompanyUserEntity> findFirstByAccountIdAndCompanyIdIsNotAndStatusOrderByStatus(String accountId, UUID companyId, Integer status);

    Integer countAllByCompanyIdAndStatusAndAccountIdIsNotNull(UUID companyId, Integer status);

    @Procedure(name = "CompanyUserEntity.transferDataToAnotherUser")
    void transferDataToAnotherUser(@Param("sourceAccountId") String sourceAccountId, @Param("destinationAccountId") String destinationAccountId, @Param("companyId") String companyId);

    @Procedure(name = "CompanyUserEntity.deactivateAllLinkByAccountIdAndCompanyId")
    void deactivateAllLinkByAccountIdAndCompanyId(@Param("accountId") String accountId, @Param("companyId") String companyId);

    @Procedure(name = "CompanyUserEntity.reactiveAllLinkByAccountIdAndCompanyId")
    void reactivateAllLinkByAccountIdAndCompanyId(@Param("accountId") String accountId, @Param("companyId") String companyId);

    @Query("select cu from CompanyUserEntity cu where cu.companyId = :companyId and cu.member_type = :member_type and cu.accountId is not null"
    )
    Optional<CompanyUserEntity> findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNull(UUID companyId, @Param("member_type") Integer member_type);

    Optional<Collection<CompanyUserEntity>> findAllByInvitationStatus(Integer invitationStatus);

    @Query(value = "EXECUTE [dbo].[sel_teammate_with_statistic_data] :numOfDay, :companyId, :page, :pageSize", nativeQuery = true)
    Optional<Collection<TeammateStatistic>> findTeammateWithStatisticData(@Param("numOfDay") Integer numOfDay,
                                                                          @Param("companyId") String companyId,
                                                                          @Param("page") Integer page,
                                                                          @Param("pageSize") Integer pageSize);

    @Query("select cu from CompanyUserEntity cu " +
            "inner join AccountEntity a on cu.accountId=a.id " +
            "where cu.status = :status and cu.accountId is not null and a.sendDailySummary=true"
    )
    Optional<Collection<CompanyUserEntity>> findAllByStatusAndAccountIdIsNotNullAndSendDailySummaryIsTrue(@Param("status") Integer status);

    @Query("select cu from CompanyUserEntity cu " +
            "inner join AccountEntity a on cu.accountId=a.id " +
            "where cu.status = :status and cu.accountId is not null and a.sendWeeklySummary=true"
    )
    Optional<Collection<CompanyUserEntity>> findAllByStatusAndAccountIdIsNotNullAndSendWeeklySummaryIsTrue(@Param("status") Integer status);
}