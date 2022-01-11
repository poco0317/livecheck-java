package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategoryId;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.BlacklistedCategoryRepo;

@Service
public class BlacklistedCategoryService {

	private static final Logger m_logger = LoggerFactory.getLogger(BlacklistedCategoryService.class);
	
	@Autowired
	private ServerConfigService configService;

	@Autowired
	private BlacklistedCategoryRepo categoryRepo;
	
	public BlacklistedCategory get(Long guildId, String category) {
		ServerConfiguration config = configService.getConfig(guildId);
		BlacklistedCategoryId id = new BlacklistedCategoryId(category, config);
		try {
			return categoryRepo.getById(id);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
	
	public boolean add(Long guildId, String category) {
		BlacklistedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			categoryWatch = new BlacklistedCategory();
			categoryWatch.setId(new BlacklistedCategoryId(category, configService.getConfig(guildId)));
			categoryRepo.save(categoryWatch);
			m_logger.info("Guild {} added blacklist category {}", guildId, category);
			return true;
		} else {
			m_logger.info("Guild {} attempted to add duplicate blacklist category {}", guildId, category);
			return false;
		}
	}
	
	public boolean remove(Long guildId, String category) {
		BlacklistedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			m_logger.info("Guild {} attempted to delete blacklist category {} (did not exist)", guildId, category);
			return false;
		} else {
			categoryRepo.delete(categoryWatch);
			m_logger.info("Guild {} deleted blacklist category {} successfully)", guildId, category);
			return true;
		}
	}
	
	public Long delAll(Long guildId) {
		Long deleted = categoryRepo.deleteByBlacklistedCategoryIdGuildId(guildId);
		m_logger.info("Guild {} deleted all categories from blacklist (count {})", guildId, deleted);
		return deleted;
	}
	
	public List<BlacklistedCategory> getAll(Long guildId) {
		List<BlacklistedCategory> list = categoryRepo.findByBlacklistedCategoryIdGuildId(guildId);
		m_logger.info("Guild {} displayed all categories in blacklist (count {})", guildId, list.size());
		return list;
	}
}
