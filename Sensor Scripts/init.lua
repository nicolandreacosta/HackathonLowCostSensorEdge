print("Setting up WIFI...")
wifi.setmode(wifi.STATION)

wifi.sta.config("ConiuTetteRing","Hackathon")

wifi.sta.connect()

tmr.alarm(1, 1000, 1, function() 
    if wifi.sta.getip()== nil then 
        print("IP unavaiable - Wifi Status: " ..wifi.sta.status() .. " - Waiting...") 
    else 
        tmr.stop(1)
		print("Config done, IP is "..wifi.sta.getip())
		dofile("setTimeSNTP.lua")
    end 
end)
