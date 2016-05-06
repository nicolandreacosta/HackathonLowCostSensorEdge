------------------------------------------
--DATA ACQUISITION AND WEB SERVICE REQUEST

url = "https://192.168.43.54:8443/testserver/addSample"

DHT_Humidity = 0
DHT_Temperature = 0
DHT_Quality = 0

DHT_PIN = 4
DHT_Attempt = 0


BMP_Pressure = 0
BMP_Temperature= 0
BMP_Quality = 0

BMP_OSS = 2 -- oversampling setting (0-3)
BMP_SCL_PIN = 1 -- scl pin, GPIO15
BMP_SDA_PIN = 2 -- sda pin, GPIO2


TLS_LUX = 0
TLS_LUX_Quality = 0

TLS_SCL_PIN = 5
TLS_SDA_PIN = 3


deviceID = "homeTemperatureMonitor00"
authSAS = "SharedAccessSignature sr=gorniHub.azure-devices.net%2fdevices%2fhomeTemperatureMonitor00&sig=Movp3KUMhtlOzWF3ssXWBELghNPcpjS2XtHJJ81bU6E%3d&se=1489486567"
		
	
function getLuxValue()
	
	local status = tsl2561.init(TLS_SDA_PIN, TLS_SCL_PIN, tsl2561.ADDRESS_FLOAT, tsl2561.PACKAGE_T_FN_CL)

	if status == tsl2561.TSL2561_OK then
		status = tsl2561.settiming(tsl2561.INTEGRATIONTIME_101MS, tsl2561.GAIN_1X)

		if status == tsl2561.TSL2561_OK then		
			TLS_LUX = tsl2561.getlux()
			TLS_LUX_Quality = 100
		else
			TLS_LUX_Quality = 0
		end
	else
		TLS_LUX_Quality = 0
	end
	
	print("Illuminance: " .. TLS_LUX .. " lx")
	print("Illuminance Quality: " .. TLS_LUX_Quality) 

end
	

function getTempDHT()
	local Humidity = 0
    local HumidityDec=0
    local Temperature = 0
    local TemperatureDec=0
	local status
	
	if ( DHT_Attempt < 5 ) then
		
		DHT_Attempt = DHT_Attempt + 1
				
		status, Temperature, Humidity, TemperatureDec, HumidityDec = dht.read11(DHT_PIN)
				
		if status == dht.OK then		
			DHT_Attempt = 0
		
			--for the time being this sensor doesn't have decimals
			DHT_Temperature = Temperature
			DHT_Humidity = Humidity		
			DHT_Quality = 100
								
		elseif status == dht.ERROR_CHECKSUM then
			print( "DHT Checksum error." )
			getTempDHT()
		elseif status == dht.ERROR_TIMEOUT then
			print( "DHT timed out." )
			getTempDHT()
		end	
		
	else
		DHT_Quality = 0
	end
	
	print("DHT Temperature: " .. DHT_Temperature)
	print("DHT Humidity: " .. DHT_Humidity)
	print("DHT Quality: " .. DHT_Quality)	
end


function getPressureBMP180()
	BMP_Pressure = 0
	BMP_Temperature = 0
	
	bmp180 = require("bmp180")
	bmp180.init(BMP_SDA_PIN, BMP_SCL_PIN)
	bmp180.read(BMP_OSS)
	t = bmp180.getTemperature()
	p = bmp180.getPressure()
	
	BMP_Pressure  = (p / 100)
	BMP_Temperature  = (t / 10)
	BMP_Quality = 100
	
	-- temperature in degrees Celsius  and Farenheit
	print("BMP Temperature: ".. BMP_Temperature .." deg C")
	-- pressure in differents units
	print("BMP Pressure: ".. BMP_Pressure .." mbar")	
	print("BMP Quality: ".. BMP_Quality)	
end


function collectData()
    
	print("-------------------")
	print("Executing Capture Data")
	print("-------------------")
		
	local timeNow = rtctime.get()	
	timeNow = 	timeNow .. "000"
	print("Time now is " .. timeNow)
		
	getTempDHT()
	getPressureBMP180()	
	getLuxValue()	
	
	callWebService(timeNow)
	
end


function callWebService(timeNow)

	print("-------------------")
	print("Executing Send Data")
	print("-------------------")
					
	print("Data from sensors retreived... sending to the Predix Machine...")		
				
	--local url = "http://casagorni.azurewebsites.net/IoTManagement.svc/PushDataMessage"			
	print("Url: " .. url)		
		
	local bodyRequest = "{\"DeviceID\":\"LowCostSensor\",\"AuthToken\":\"theAuthToken\",\"Samples\":\[" ..
		"{ \"PointName\":\"DHT_Temperature\",\"Quality\":\"".. DHT_Quality .."\",\"TimeStamp\":\"" .. timeNow .. "\",\"Value\":\""  .. DHT_Temperature ..  "\" }" ..
		",{ \"PointName\":\"DHT_Humidity\",\"Quality\":\"".. DHT_Quality .."\",\"TimeStamp\":\"" .. timeNow .. "\",\"Value\":\""  .. DHT_Humidity ..  "\" }" ..
		",{ \"PointName\":\"BMP_Pressure\",\"Quality\":\"".. BMP_Quality .."\",\"TimeStamp\":\"" .. timeNow .. "\",\"Value\":\""  .. BMP_Pressure ..  "\" }" ..
		",{ \"PointName\":\"BMP_Temperature\",\"Quality\":\"".. BMP_Quality .."\",\"TimeStamp\":\"" .. timeNow .. "\",\"Value\":\""  .. BMP_Temperature ..  "\" }" ..
		",{ \"PointName\":\"TLS_LUX\",\"Quality\":\"".. TLS_LUX_Quality .."\",\"TimeStamp\":\"" .. timeNow .. "\",\"Value\":\""  .. TLS_LUX ..  "\" }" ..
		"\]}"
	
	print("Body:\r\n" .. bodyRequest)
	
	local bodyLengh = string.len(bodyRequest)
	
	local header = "Content-Type: application/json\r\n" 
	.. 	 	 "Content-Length: " .. bodyLengh .. "\r\n"

	print("Header:\r\n" .. header)		
			
	print("-------------------")
	print("Sending Data...")
	print("-------------------")
			
	http.put(url,header,bodyRequest,
		  function(code, data)
			if (code < 0) then
				print("-------------------")
				print("HTTP request failed\r\n")
				print("-------------------")
				callWebService(timeNow)
			else
				print("-------------------")
				print("Data SENT!!")					
				print("HTTP Response Code: " .. code .. "\r\n")
				print("Response:\r\n")
				print(data)				
				print("-------------------")
			end
		  end)      
    
	print("-------------------")
	print("Web Service called... waiting for response...")
	print("-------------------")
	
end


print("Setting and starting the polling Timer...")
tmr.alarm(2, 15000, 1, function() collectData() end )
--and start immediately the collection of the data!
collectData()
