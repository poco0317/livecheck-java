package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.pk.WhitelistedChannelId;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.WhitelistedChannelRepo;

@Service
public class WhitelistedChannelService {

	private static final Logger m_logger = LoggerFactory.getLogger(WhitelistedChannelService.class);
	
	@Autowired
	private ServerConfigService configService;

	@Autowired
	private WhitelistedChannelRepo channelRepo;
	
	@Transactional
	public WhitelistedChannel get(Long guildId, String channel) {
		ServerConfiguration config = configService.getConfig(guildId);
		WhitelistedChannelId id = new WhitelistedChannelId(channel, config);
		return channelRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public boolean add(Long guildId, String channel) {
		WhitelistedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			channelWatch = new WhitelistedChannel();
			channelWatch.setId(new WhitelistedChannelId(channel, configService.getConfig(guildId)));
			channelRepo.save(channelWatch);
			m_logger.info("Guild {} added whitelist channel {}", guildId, channel);
			return true;
		} else {
			m_logger.info("Guild {} attempted to add duplicate whitelist channel {}", guildId, channel);
			return false;
		}
	}
	
	@Transactional
	public boolean remove(Long guildId, String channel) {
		WhitelistedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			m_logger.info("Guild {} attempted to delete whitelist channel {} (did not exist)", guildId, channel);
			return false;
		} else {
			channelRepo.delete(channelWatch);
			m_logger.info("Guild {} deleted whitelist channel {} successfully)", guildId, channel);
			return true;
		}
	}
	
	@Transactional
	public Long delAll(Long guildId) {
		Long deleted = channelRepo.deleteByIdGuildId(guildId);
		m_logger.info("Guild {} deleted all categories from whitelist (count {})", guildId, deleted);
		return deleted;
	}
	
	@Transactional
	public List<WhitelistedChannel> getAll(Long guildId) {
		List<WhitelistedChannel> list = channelRepo.findByIdGuildId(guildId);
		m_logger.info("Guild {} displayed all categories in whitelist (count {})", guildId, list.size());
		return list;
	}
}
