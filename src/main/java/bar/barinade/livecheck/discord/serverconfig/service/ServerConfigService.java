package bar.barinade.livecheck.discord.serverconfig.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.ServerConfigurationRepo;
@Service
public class ServerConfigService {
	
	private static final Logger m_logger = LoggerFactory.getLogger(ServerConfigService.class);

	@Autowired
	private ServerConfigurationRepo configRepo;
	
	@Transactional
	public ServerConfiguration getConfig(Long guildId) {
		ServerConfiguration config = configRepo.findById(guildId).orElse(null);
		if (config == null) {
			config = new ServerConfiguration();
			config.setId(guildId);
			config = configRepo.saveAndFlush(config);
		}
		return config;
	}
	
	@Transactional
	public void setRequiredTitleRegex(Long guildId, String regex) {
		ServerConfiguration config = getConfig(guildId);
		config.setTitleRegex(regex);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set title regex to {}", guildId, regex);
	}
	
	@Transactional
	public String getRequiredTitleRegex(Long guildId) {
		return getConfig(guildId).getTitleRegex();
	}
	
	@Transactional
	public void setOutputChannel(Long guildId, Long channelId) {
		ServerConfiguration config = getConfig(guildId);
		config.setChannelId(channelId);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set output channel to {}", guildId, channelId);
	}
	
	@Transactional
	public Long getOutputChannel(Long guildId) {
		return getConfig(guildId).getChannelId();
	}
	
	@Transactional
	public void setMentionRole(Long guildId, Long roleId) {
		ServerConfiguration config = getConfig(guildId);
		config.setMentionRoleId(roleId);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set live-mention role to {}", guildId, roleId);
	}
	
	@Transactional
	public Long getMentionRole(Long guildId) {
		return getConfig(guildId).getMentionRoleId();
	}
	
}
