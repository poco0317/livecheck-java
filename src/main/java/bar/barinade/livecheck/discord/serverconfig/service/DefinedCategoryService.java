package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategoryId;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.DefinedCategoryRepo;

@Service
public class DefinedCategoryService {

	private static final Logger m_logger = LoggerFactory.getLogger(DefinedCategoryService.class);
	
	@Autowired
	private ServerConfigService configService;

	@Autowired
	private DefinedCategoryRepo categoryRepo;
	
	@Transactional
	public DefinedCategory get(Long guildId, String category) {
		ServerConfiguration config = configService.getConfig(guildId);
		DefinedCategoryId id = new DefinedCategoryId(category, config);
		return categoryRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public boolean add(Long guildId, String category) {
		DefinedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			categoryWatch = new DefinedCategory();
			categoryWatch.setId(new DefinedCategoryId(category, configService.getConfig(guildId)));
			categoryRepo.save(categoryWatch);
			m_logger.info("Guild {} added watchlist category {}", guildId, category);
			return true;
		} else {
			m_logger.info("Guild {} attempted to add duplicate watchlist category {}", guildId, category);
			return false;
		}
	}
	
	@Transactional
	public boolean remove(Long guildId, String category) {
		DefinedCategory categoryWatch = get(guildId, category);
		if (categoryWatch == null) {
			m_logger.info("Guild {} attempted to delete watchlist category {} (did not exist)", guildId, category);
			return false;
		} else {
			categoryRepo.delete(categoryWatch);
			m_logger.info("Guild {} deleted watchlist category {} successfully)", guildId, category);
			return true;
		}
	}
	
	@Transactional
	public Long delAll(Long guildId) {
		Long deleted = categoryRepo.deleteByIdGuildId(guildId);
		m_logger.info("Guild {} deleted all categories from watch list (count {})", guildId, deleted);
		return deleted;
	}
	
	@Transactional
	public List<DefinedCategory> getAll(Long guildId) {
		List<DefinedCategory> list = categoryRepo.findByIdGuildId(guildId);
		m_logger.info("Guild {} displayed all categories in watch list (count {})", guildId, list.size());
		return list;
	}
}
