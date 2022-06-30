package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.models.CompanyOfUser;
import logixtek.docsoup.api.infrastructure.models.UserCompanyWithPlanTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    @Query("SELECT c.id as id, c.name as name from CompanyEntity c " +
            "INNER JOIN CompanyUserEntity cu on c.id = cu.companyId " +
            "where cu.accountId = :accountId and cu.status = :status")
    Optional<Collection<CompanyOfUser>> findAllByAccountIdAndCompanyUserStatus(@Param("accountId") String accountId, @Param("status") Integer status);

    @Query("SELECT cu.companyId AS companyId, c.name AS name, cu.createdDate AS joinDate, pt.name as planTier, cu.member_type as memberType, cu.role as role, cu.status AS status " +
            "FROM CompanyUserEntity cu " +
            "INNER JOIN CompanyEntity c ON c.id = cu.companyId " +
            "LEFT JOIN PlanTierEntity pt ON pt.id = c.planTierId " +
            "WHERE cu.accountId = :accountId")
    Optional<Collection<UserCompanyWithPlanTier>> getUserCompanyWithPlanTier(@Param("accountId") String accountId);

    @Query("SELECT c.id as id, c.name as name from CompanyEntity c " +
            "INNER JOIN CompanyUserEntity cu on c.id = cu.companyId " +
            "where cu.accountId = :accountId and cu.status = :status and c.id = :companyId")
    Optional<CompanyOfUser> findByIdAndAccountIdAndUserStatus(UUID companyId, String accountId, Integer status);

    long countByPlanTierId(Long planTierId);
}