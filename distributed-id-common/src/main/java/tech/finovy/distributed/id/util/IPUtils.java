package tech.finovy.distributed.id.util;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class IPUtils {

    private static final String LOCALHOST = "127.0.0.1";

    private static final String ANY_HOST = "0.0.0.0";

    private static volatile InetAddress LOCAL_ADDRESS = null;

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static String getIp() {
        String ip;
        try {
            List<String> ipList = getHostAddress(null);
            // default the first
            ip = (!ipList.isEmpty()) ? ipList.get(0) : "";
        } catch (Exception ex) {
            ip = "";
            log.warn("Utils get IP warn", ex);
        }
        return ip;
    }

    /**
     * 获取已激活网卡的IP地址
     *
     * @param interfaceName 可指定网卡名称,null则获取全部
     * @return List<String>
     */
    private static List<String> getHostAddress(String interfaceName) throws SocketException {
        List<String> ipList = new ArrayList<String>(5);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> allAddress = ni.getInetAddresses();
            while (allAddress.hasMoreElements()) {
                InetAddress address = allAddress.nextElement();
                if (address.isLoopbackAddress()) {
                    // skip the loopback addr
                    continue;
                }
                if (address instanceof Inet6Address) {
                    // skip the IPv6 addr
                    continue;
                }
                String hostAddress = address.getHostAddress();
                if (null == interfaceName) {
                    ipList.add(hostAddress);
                } else if (interfaceName.equals(ni.getDisplayName())) {
                    ipList.add(hostAddress);
                }
            }
        }
        return ipList;
    }

    /**
     * Gets local ip.
     *
     * @return the local ip
     */
    public static String getLocalIp() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    /**
     * Gets local address.
     *
     * @return the local address
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                            }
                        }
                    } catch (Throwable e) {
                        log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
        }
        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    /**
     * is valid address
     *
     * @param address
     * @return true if the given address is valid
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        return isValidIp(address.getHostAddress(), false);
    }

    /**
     * is valid IP
     *
     * @param ip
     * @param validLocalAndAny Are 127.0.0.1 and 0.0.0.0 valid IPs?
     * @return true if the given IP is valid
     */
    public static boolean isValidIp(String ip, boolean validLocalAndAny) {
        if (ip == null) {
            return false;
        }
        ip = convertIpIfNecessary(ip);
        if (validLocalAndAny) {
            return IP_PATTERN.matcher(ip).matches();
        } else {
            return !ANY_HOST.equals(ip) && !LOCALHOST.equals(ip) && IP_PATTERN.matcher(ip).matches();
        }

    }

    /**
     * convert ip if necessary
     *
     * @param ip
     * @return java.lang.String
     */
    private static String convertIpIfNecessary(String ip) {
        if (IP_PATTERN.matcher(ip).matches()) {
            return ip;
        } else {
            try {
                return InetAddress.getByName(ip).getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
