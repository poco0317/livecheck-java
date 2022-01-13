package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.pk.WhitelistedCategoryId;
import bar.barinade.livecheck.discord.serverconfig.repo.WhitelistedCategoryRepo;

@Service
public class WhitelistedCategoryService {

	private static final Logger m_logger = LoggerFactory.getLogger(WhitelistedCategoryService.class);
	
	@Autowired
	private ServerConfigService configService;

	@Autowired
	private WhitelistedCategoryRepo categoryRepo;
	
	@Transactional
	public WhitelistedCategory get(Long guildId, String category) {
		ServerConfiguration config = configService.getConfig(guildId);
		WhitelistedCategoryId id = new WhitelistedCategoryId(category, config);
		return categoryRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public boolean add(Long guildId, String category) {
		WhitelistedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			categoryWatch = new WhitelistedCategory();
			categoryWatch.setId(new WhitelistedCategoryId(category, configService.getConfig(guildId)));
			categoryRepo.save(categoryWatch);
			m_logger.debug("Guild {} added whitelist category {}", guildId, category);
			return true;
		} else {
			m_logger.debug("Guild {} attempted to add duplicate whitelist category {}", guildId, category);
			return false;
		}
	}
	
	@Transactional
	public boolean remove(Long guildId, String category) {
		WhitelistedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			m_logger.debug("Guild {} attempted to delete whitelist category {} (did not exist)", guildId, category);
			return false;
		} else {
			categoryRepo.delete(categoryWatch);
			m_logger.debug("Guild {} deleted whitelist category {} successfully)", guildId, category);
			return true;
		}
	}
	
	@Transactional
	public Long delAll(Long guildId) {
		Long deleted = categoryRepo.deleteByIdGuildId(guildId);
		m_logger.debug("Guild {} deleted all categories from whitelist (count {})", guildId, deleted);
		return deleted;
	}
	
	@Transactional
	public List<WhitelistedCategory> getAll(Long guildId) {
		List<WhitelistedCategory> list = categoryRepo.findByIdGuildId(guildId);
		m_logger.trace("Guild {} displayed all categories in whitelist (count {})", guildId, list.size());
		return list;
	}
}
