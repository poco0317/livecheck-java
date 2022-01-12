package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.data.pk.BlacklistedChannelId;
import bar.barinade.livecheck.discord.serverconfig.repo.BlacklistedChannelRepo;

@Service
public class BlacklistedChannelService {

	private static final Logger m_logger = LoggerFactory.getLogger(BlacklistedChannelService.class);
	
	@Autowired
	private ServerConfigService configService;

	@Autowired
	private BlacklistedChannelRepo channelRepo;
	
	@Transactional
	public BlacklistedChannel get(Long guildId, String channel) {
		ServerConfiguration config = configService.getConfig(guildId);
		BlacklistedChannelId id = new BlacklistedChannelId(channel, config);
		return channelRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public boolean add(Long guildId, String channel) {
		BlacklistedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			channelWatch = new BlacklistedChannel();
			channelWatch.setId(new BlacklistedChannelId(channel, configService.getConfig(guildId)));
			channelRepo.save(channelWatch);
			m_logger.info("Guild {} added blacklist channel {}", guildId, channel);
			return true;
		} else {
			m_logger.info("Guild {} attempted to add duplicate blacklist channel {}", guildId, channel);
			return false;
		}
	}
	
	@Transactional
	public boolean remove(Long guildId, String channel) {
		BlacklistedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			m_logger.info("Guild {} attempted to delete blacklist channel {} (did not exist)", guildId, channel);
			return false;
		} else {
			channelRepo.delete(channelWatch);
			m_logger.info("Guild {} deleted blacklist channel {} successfully)", guildId, channel);
			return true;
		}
	}
	
	@Transactional
	public Long delAll(Long guildId) {
		Long deleted = channelRepo.deleteByIdGuildId(guildId);
		m_logger.info("Guild {} deleted all categories from blacklist (count {})", guildId, deleted);
		return deleted;
	}
	
	@Transactional
	public List<BlacklistedChannel> getAll(Long guildId) {
		List<BlacklistedChannel> list = channelRepo.findByIdGuildId(guildId);
		m_logger.info("Guild {} displayed all categories in blacklist (count {})", guildId, list.size());
		return list;
	}
}
