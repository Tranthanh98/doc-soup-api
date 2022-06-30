package logixtek.docsoup.api.infrastructure.repositories;

import java.util.Collection;
import java.util.Optional;

import logixtek.docsoup.api.infrastructure.models.PublicAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findFirstByEmail(String email);

    @Override
    Optional<AccountEntity> findById(String accountId);

    @Query(value = "EXECUTE [dbo].[sel_search_account_by_name_or_email] :keyword, :page, :pageSize ", nativeQuery = true)
    Optional<Collection<PublicAccount>> searchAccountByNameOrEmail(@Param("keyword") String keyword, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    @Query("SELECT ac.id AS id, CONCAT(ac.firstName, ' ', ac.lastName) AS fullName, ac.email AS email, " +
            "coalesce(ac.checkInTime, ac.createdDate) AS lastActive, ac.activeCompanyId AS activeCompanyId, " +
            "ac.phone AS phone, ac.createdDate AS checkInDate, SUM(fe.size) AS usedSpace, ac.enable AS enable " +
            "FROM AccountEntity ac " +
            "LEFT JOIN FileEntity fe ON fe.accountId = ac.id " +
            "WHERE ac.id = :accountId " +
            "GROUP BY ac.id, ac.firstName, ac.lastName, ac.email, ac.checkInTime, ac.activeCompanyId, ac.phone, ac.createdDate, ac.enable")
    Optional<PublicAccount> findDetailedAccountById(@Param("accountId") String accountId );


}