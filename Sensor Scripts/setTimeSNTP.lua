--set timne using NTP server
print("Setting Time...")

ntpServer = 'it.pool.ntp.org'

sntp.sync(ntpServer,
  function(sec,usec,server)
    print('sync', sec, usec, server)
	dofile("uploadData.lua")
  end,
  function()
	print('failed!')
	dofile("setTimeSNTP.lua")
  end
)