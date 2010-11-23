/*
 * Copyright (c) 2008-2010. Agwego Enterprises Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * If you modify this software a credit would be nice
 */

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.applet.Applet;

public class MacAddressApplet extends Applet
{
    public static String sep = ":";
    public static String format = "%02X";

    /**
     * getMacAddress - return the first mac address found
     * separator - byte seperator default ":"
     * format - byte formatter default "%02X"
     *
     * @param ni - the network interface
     * @return String - the mac address as a string
     * @throws SocketException - pass it on
     */
    public static String macToString( NetworkInterface ni ) throws SocketException
    {
        return macToString( ni, MacAddressApplet.sep,  MacAddressApplet.format );
    }

    /**
     * getMacAddress - return the first mac address found
     *
     * @param ni - the network interface
     * @param separator - byte seperator default ":"
     * @param format - byte formatter default "%02X"
     * @return String - the mac address as a string
     * @throws SocketException - pass it on
     */
    public static String macToString( NetworkInterface ni, String separator, String format ) throws SocketException
    {
        byte mac [] = ni.getHardwareAddress();

        if( mac != null ) {
            StringBuffer macAddress = new StringBuffer( "" );
            String sep = "";
            for( byte o : mac ) {
                macAddress.append( sep ).append( String.format( format, o ) );
                sep = separator;
            }
            return macAddress.toString();
        }

        return null;
    }

	/**
	 * getMacAddressInternal - return the first mac address found
	 *
	 * @return the mac address or undefined
	 */
	protected static String getMacAddressInternal()
	{
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

			// not all interface will have a mac address for instance loopback on windows
			while( nis.hasMoreElements() ) {
				String mac = macToString( nis.nextElement() );
				if( mac != null && mac.length() > 0 )
					return mac;
			}
		} catch( SocketException ex ) {
			System.err.println( "SocketException:: " + ex.getMessage() );
			ex.printStackTrace();
		} catch( Exception ex ) {
			System.err.println( "Exception:: " + ex.getMessage() );
			ex.printStackTrace();
		}

		return "undefined";
	}

    /**
     * getMacAddressesJSON - return all mac addresses found
     *
     * @return a JSON array of strings (as a string)
     */
    protected static String getMacAddressesJSONInternal()
    {
        try {
            String macs [] = getMacAddresses();

            String sep = "";
            StringBuffer macArray = new StringBuffer( "['" );
            for( String mac: macs ) {
                macArray.append( sep ).append( mac );
                sep = "','";
            }
            macArray.append( "']" );

            return macArray.toString();
        } catch( Exception ex ) {
            System.err.println( "Exception:: " + ex.getMessage() );
            ex.printStackTrace();
        }

        return "[]";
    }

    /**
     * getMacAddresses - return all mac addresses found
     *
     * @return array of strings (mac addresses) empty if none found
     */
    public static String [] getMacAddresses()
    {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            
            ArrayList<String> macs = new ArrayList<String>();
            while( nis.hasMoreElements() ) {
                String mac = macToString( nis.nextElement() );
                // not all interface will have a mac address for instance loopback on windows
                if( mac != null ) {
                    macs.add( mac );
                }
            }
            return macs.toArray( new String[macs.size()] );
        } catch( SocketException ex ) {
            System.err.println( "SocketException:: " + ex.getMessage() );
            ex.printStackTrace();
        } catch( Exception ex ) {
            System.err.println( "Exception:: " + ex.getMessage() );
            ex.printStackTrace();
        }

        return new String[0];
    }

    /**
     * getMacAddresses - return all mac addresses found
     *
     * @param sep - use a different separator
     */
    public static void setSep( String sep )
    {
        try {
            MacAddressApplet.sep = sep;
        } catch( Exception ex ) {
            //  don't care
        }
    }

    /**
     * getMacAddresses - return all mac addresses found
     *
     * @param format - the output format string for bytes that can be overridden default hex.
     */
    public static void setFormat( String format )
    {
        try {
            MacAddressApplet.format = format;
        } catch( Exception ex ) {
            //  don't care
        }
    }

	/**
	 * Wrap the privilege access to our internal method
	 */
	private final class MacAddressService implements PrivilegedAction<String>
	{
		public String run()
		{
			return getMacAddressInternal();
		}
	}

	/**
	 * Wrap the privilege access to our internal method
	 */
	private final class MacAddressesJSONService implements PrivilegedAction<String>
	{
		public String run()
		{
			return getMacAddressesJSONInternal();
		}
	}


	/**
	 * getMacAddress - return the first mac address found
	 *
	 * @return the mac address or undefined
	 * @throws java.security.PrivilegedActionException ex
	 */
	public String getMacAddress() throws PrivilegedActionException
	{
		return AccessController.doPrivileged( new MacAddressService() );
	}

	/**
	 * getMacAddress - return the first mac address found
	 *
	 * @return the mac address or undefined
	 * @throws java.security.PrivilegedActionException ex
	 */
	public String getMacAddressesJSON() throws PrivilegedActionException
	{
		return AccessController.doPrivileged( new MacAddressesJSONService() );
	}

    public static void main( String... args )
    {
	    MacAddressApplet ma = new MacAddressApplet();

	    try {
            System.err.println( " MacAddress = " + ma.getMacAddress() );
		    System.err.println( " MacAddresses JSON = " + ma.getMacAddressesJSON() );

	    } catch( PrivilegedActionException ex ) {
		    System.err.println( ex );
		    ex.printStackTrace();
	    }
    }
}