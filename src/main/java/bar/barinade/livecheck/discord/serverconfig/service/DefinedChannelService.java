package bar.barinade.livecheck.discord.serverconfig.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.DefinedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.DefinedChannelId;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.DefinedChannelRepo;

@Service
public class DefinedChannelService {
	
	private static final Logger m_logger = LoggerFactory.getLogger(DefinedChannelService.class);
	
	@Autowired
	private ServerConfigService configService;
	
	@Autowired
	private DefinedChannelRepo channelRepo;
	
	@Transactional
	public DefinedChannel get(Long guildId, String channel) {
		ServerConfiguration config = configService.getConfig(guildId);
		DefinedChannelId id = new DefinedChannelId(channel, config);
		return channelRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public boolean add(Long guildId, String channel) {
		DefinedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			channelWatch = new DefinedChannel();
			channelWatch.setId(new DefinedChannelId(channel, configService.getConfig(guildId)));
			channelRepo.save(channelWatch);
			m_logger.info("Guild {} added watchlist channel {}", guildId, channel);
			return true;
		} else {
			m_logger.info("Guild {} attempted to add duplicate watchlist channel {}", guildId, channel);
			return false;
		}
	}
	
	@Transactional
	public boolean remove(Long guildId, String channel) {
		DefinedChannel channelWatch = get(guildId, channel);
		if (channelWatch == null) {
			m_logger.info("Guild {} attempted to delete watchlist channel {} (did not exist)", guildId, channel);
			return false;
		} else {
			channelRepo.delete(channelWatch);
			m_logger.info("Guild {} deleted watchlist channel {} successfully)", guildId, channel);
			return true;
		}
	}
	
	@Transactional
	public Long delAll(Long guildId) {
		Long deleted = channelRepo.deleteByIdGuildId(guildId);
		m_logger.info("Guild {} deleted all channels from watch list (count {})", guildId, deleted);
		return deleted;
	}
	
	@Transactional
	public List<DefinedChannel> getAll(Long guildId) {
		List<DefinedChannel> list = channelRepo.findByIdGuildId(guildId);
		m_logger.info("Guild {} displayed all channels in watch list (count {})", guildId, list.size());
		return list;
	}

}
