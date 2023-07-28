package com.ruoyi.utils;

import com.ruoyi.domain.Device;
import com.ruoyi.sip_server.config.SipConfig;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.stack.SIPServerTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Component
public class SipUtil {

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;

    @Autowired
    private SipStack sipStack;

    @Autowired
    private SipCmdUtil sipCmdUtil;

    @Autowired
    private SipProvider tcpSipProvider;

    @Autowired
    private SipProvider udpSipProvider;



    /**
     * 发送响应
     *
     * @param evt        请求事件对象
     * @param resp       响应对象,为空则创建一个否则使用传进来的
     * @param statusCode resp为null的时候根据此值设置resp Response.OK
     * @param msg        消息 添加到resp的,为null则不添加
     */
    public void response(RequestEvent evt, Response resp, Integer statusCode, String msg) {
        try {
            if (resp == null && statusCode == null) throw new RuntimeException("参数不合法");
            // 为空则创建 | 使用传进来的
            if (resp == null) resp = sipFactory.createMessageFactory().createResponse(statusCode, evt.getRequest());
            if (StringUtils.hasText(msg)) resp.setReasonPhrase(msg);
            ServerTransaction serverTransaction = getServerTransaction(evt);
            serverTransaction.sendResponse(resp);
            int code = resp.getStatusCode();
            if (code >= 200 && !"NOTIFY".equals(evt.getRequest().getMethod())) {
                if (serverTransaction.getDialog() != null) {
                    serverTransaction.getDialog().delete();
                }
            }
        } catch (ParseException | SipException | InvalidArgumentException e) {
            log.error("\n发送响应错误{}", e);
            e.printStackTrace();
        }
    }


    /**
     * SDP 转 Properties
     *
     * @param content byte[] 数组
     * @return
     */
    public Properties getProperties(byte[] content) {
        Properties p = new Properties();
        try {
            p.load(new ByteArrayInputStream(content));
            return p;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("\n处理错误: {}", e);
            return null;
        }
    }


    /**
     * 根据协议类型获取 ServerTransaction
     *
     * @param req Request
     * @return
     */
    public ServerTransaction getServerTransaction(Request req) {
        try {
            return (this.isTcp(req) ? tcpSipProvider : udpSipProvider).getNewServerTransaction(req);
        } catch (TransactionAlreadyExistsException | TransactionUnavailableException e) {
            e.printStackTrace();
            log.error("\n处理错误: {}", e);

        }
        return null;
    }

    /**
     * 根据协议类型获取 ServerTransaction
     *
     * @param req Request
     * @return
     */
    public ClientTransaction getClientTransaction(Request req) {
        try {
            return (this.isTcp(req) ? tcpSipProvider : udpSipProvider).getNewClientTransaction(req);
        } catch (TransactionUnavailableException e) {
            log.error("\n getClientTransaction处理错误: {}", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据 RequestEvent 获取 ServerTransaction
     *
     * @param evt
     * @return
     */
    public ServerTransaction getServerTransaction(RequestEvent evt) {
        Request request = evt.getRequest();
        ServerTransaction serverTransaction = evt.getServerTransaction();

        if (serverTransaction == null) {
            try {
                if (isTcp(request)) {

                    SipStackImpl stack = (SipStackImpl) tcpSipProvider.getSipStack();
                    serverTransaction = (SIPServerTransaction) stack.findTransaction((SIPRequest) request, true);
                    if (serverTransaction == null) {
                        serverTransaction = tcpSipProvider.getNewServerTransaction(request);
                    }
                } else {
                    SipStackImpl stack = (SipStackImpl) udpSipProvider.getSipStack();
                    serverTransaction = (SIPServerTransaction) stack.findTransaction((SIPRequest) request, true);
                    if (serverTransaction == null) {
                        serverTransaction = udpSipProvider.getNewServerTransaction(request);
                    }
                }
            } catch (TransactionAlreadyExistsException | TransactionUnavailableException e) {
                log.error("\n处理错误: {}", e);
                e.printStackTrace();
            }
        }
        return serverTransaction;
    }


    /**
     * 判断是Tcp还是 UDP
     *
     * @param request
     * @return boolean
     */
    public boolean isTcp(Message request) {
        boolean isTcp = false;
        ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
        String transport = reqViaHeader.getTransport();
        if (transport.equalsIgnoreCase("TCP")) {
            isTcp = true;
        }
        return isTcp;
    }

    /**
     * 发送SIP请求
     *
     * @param protocol 协议(UDP|TCP)
     * @param request  请求对象
     * @throws SipException
     */
    public void transmitRequest(String protocol, Request request) throws SipException {
        ClientTransaction clientTransaction = null;
        if ("TCP".equalsIgnoreCase(protocol)) {
            clientTransaction = tcpSipProvider.getNewClientTransaction(request);
        } else if ("UDP".equalsIgnoreCase(protocol)) {
            clientTransaction = udpSipProvider.getNewClientTransaction(request);
        }
        if (clientTransaction != null) {
            clientTransaction.sendRequest();
            log.info("\n请求设备信息发送成功");

        } else {
            log.error("\nClientTransaction为空");
        }
    }


    /**
     * 创建 Invite
     *
     * @param device       设备对象
     * @param channelId    暂用通道ID
     * @param msg          消息体
     * @param viaTag       会话标签
     * @param fromTag      发起方标签
     * @param toTag        目标方标签
     * @param ssrc         SSRC
     * @param callIdHeader Call-ID
     * @return
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws PeerUnavailableException
     */
    public Request createInviteRequest(Device device, String channelId, String msg, String viaTag, String fromTag, String toTag, String ssrc, CallIdHeader callIdHeader) throws ParseException, InvalidArgumentException, PeerUnavailableException {
        Request request = null;
        //请求行
        SipURI requestLine = sipFactory.createAddressFactory().createSipURI(channelId, String.format("%s:%s", device.getDeviceIp(), device.getDevicePort()));
        //via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(sipConfig.getIp(), sipConfig.getPort(), device.getRegisterProtocol(), viaTag);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);

        //from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipConfig.getDomain());
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag); //必须要有标记，否则无法创建会话，无法回应ack
        //to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(channelId, sipConfig.getDomain());
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, null);

        //Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);

        //ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(/*redisUtil.getCseq(Request.INVITE)*/1L, Request.INVITE);
        request = sipFactory.createMessageFactory().createRequest(requestLine, Request.INVITE, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

        Address concatAddress = sipFactory.createAddressFactory().createAddress(sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipConfig.getListenIp() + ":" + sipConfig.getPort()));
        // Address concatAddress = sipFactory.createAddressFactory().createAddress(sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), device.getHost().getIp()+":"+device.getHost().getPort()));
        request.addHeader(sipFactory.createHeaderFactory().createContactHeader(concatAddress));
        // Subject
        SubjectHeader subjectHeader = sipFactory.createHeaderFactory().createSubjectHeader(String.format("%s:%s,%s:%s", channelId, ssrc, sipConfig.getId(), 0));
        request.addHeader(subjectHeader);
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("APPLICATION", "SDP");
        request.setContent(msg, contentTypeHeader);
        return request;
    }


    /**
     * 创建消息请求
     *
     * @param d       设备对象
     * @param fromTag      发起方标签
     * @param callIdHeader Call-ID
     * @return
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws PeerUnavailableException
     */
    public Request createMessageRequest(Device d, String content, String fromTag,String viaBranch, CallIdHeader callIdHeader) throws PeerUnavailableException, ParseException, InvalidArgumentException {
        Request request = null;

        String sipAddress = sipConfig.getIp() + ":" + sipConfig.getPort();
        String deviceAddress = d.getDeviceIp() + ":" + d.getDevicePort();
        //请求行
        SipURI requestLine = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(),
                sipConfig.getIp() + ":" + sipConfig.getPort());
        // sipuri
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipAddress);
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(d.getDeviceIp(), d.getDevicePort(), d.getRegisterProtocol(), viaBranch);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(d.getDeviceId(), deviceAddress);
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        // to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipAddress);
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, null);
        // Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);
        MessageFactoryImpl messageFactory = (MessageFactoryImpl) sipFactory.createMessageFactory();
        // 设置编码， 防止中文乱码
        messageFactory.setDefaultContentEncodingCharset(d.getCharset());
        request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        List<String> agentParam = new ArrayList<>();
        agentParam.add("VM-gb-client");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("APPLICATION", "MANSCDP+xml");
        request.setContent(content, contentTypeHeader);
        return request;
    }


    /**
     * 根据已存在的会话创建请求
     *
     * @param request dialog获取的请求对象
     * @param context 请求体内容[操作命令]
     * @return
     */
    public void createMsgRequest(Request request, String context) throws SipException, ParseException {
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("Application",
                "MANSRTSP");
        request.setContent(context, contentTypeHeader);
    }


    /**
     * 设备注册
     * @param d 设备对象
     * @param CSeq 区分是否同一次会议
     * @param fromTag
     * @param viaTag
     * @param callIdHeader
     * @param exs 3600:注册 0:注销
     * @return
     * @throws PeerUnavailableException
     * @throws ParseException
     * @throws InvalidArgumentException
     */
    public Request createRegisterRequest(Device d, long CSeq, String fromTag, String viaTag, CallIdHeader callIdHeader,int exs) throws PeerUnavailableException, ParseException, InvalidArgumentException {
        Request request = null;
        String sipAddress = d.getDeviceIp() + ":" + d.getDevicePort();
        //请求行
        SipURI requestLine = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(),
                sipConfig.getIp() + ":" + sipConfig.getPort());
        //via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(sipConfig.getIp(), sipConfig.getPort(), d.getRegisterProtocol(), viaTag);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        //from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(d.getDeviceId(), sipAddress);
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        //to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(d.getDeviceId(), sipAddress);
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, null);
        //Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        //ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(CSeq, Request.REGISTER);
        request = sipFactory.createMessageFactory().createRequest(requestLine, Request.REGISTER, callIdHeader,
                cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
        Address concatAddress = sipFactory.createAddressFactory().createAddress(sipFactory.createAddressFactory()
                .createSipURI(d.getDeviceId(), sipAddress));
        request.addHeader(sipFactory.createHeaderFactory().createContactHeader(concatAddress));
        // 过期时间
        ExpiresHeader expires = sipFactory.createHeaderFactory().createExpiresHeader(exs);
        request.addHeader(expires);
        List<String> agentParam = new ArrayList<>();
        // agentParam.add("wydpp");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        return request;
    }

    /**
     * 携带3W 认证信息注册
     * @param www 认证信息
     * @param d 设备对象
     * @param fromTag
     * @param viaTag
     * @param callIdHeader
     * @return
     * @throws ParseException
     * @throws PeerUnavailableException
     * @throws InvalidArgumentException
     */
    public Request createRegisterRequest(WWWAuthenticateHeader www, Device d,  long CSeq, String fromTag, String viaTag, CallIdHeader callIdHeader) throws ParseException, PeerUnavailableException, InvalidArgumentException {
        Request registerRequest = createRegisterRequest(d,  CSeq, fromTag, viaTag, callIdHeader,3600);
        String realm = www.getRealm();
        String nonce = www.getNonce();
        String scheme = www.getScheme();
        // 参考 https://blog.csdn.net/y673533511/article/details/88388138
        // qop 保护质量 包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略
        String qop = www.getQop();
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(d.getDeviceId(), sipConfig.getIp() + ":" + sipConfig.getPort());
        String cNonce = null;
        String nc = "00000001";
        if (qop != null) {
            if ("auth".equals(qop)) {
                // 客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。
                // 这使得双方都可以查验对方的身份，并对消息的完整性提供一些保护
                cNonce = UUID.randomUUID().toString();
            } else if ("auth-int".equals(qop)) {
                // TODO
            }
        }
        String HA1 = DigestUtils.md5DigestAsHex((d.getDeviceId() + ":" + realm + ":" + sipConfig.getPassword()).getBytes());
        String HA2 = DigestUtils.md5DigestAsHex((Request.REGISTER + ":" + requestURI.toString()).getBytes());
        StringBuffer reStr = new StringBuffer(200);
        reStr.append(HA1);
        reStr.append(":");
        reStr.append(nonce);
        reStr.append(":");
        if (qop != null) {
            reStr.append(nc);
            reStr.append(":");
            reStr.append(cNonce);
            reStr.append(":");
            reStr.append(qop);
            reStr.append(":");
        }
        reStr.append(HA2);
        String RESPONSE = DigestUtils.md5DigestAsHex(reStr.toString().getBytes());
        AuthorizationHeader authorizationHeader = sipFactory.createHeaderFactory().createAuthorizationHeader(scheme);
        authorizationHeader.setUsername(d.getDeviceId());
        authorizationHeader.setRealm(realm);
        authorizationHeader.setNonce(nonce);
        authorizationHeader.setURI(requestURI);
        authorizationHeader.setResponse(RESPONSE);
        authorizationHeader.setAlgorithm("MD5");
        if (qop != null) {
            authorizationHeader.setQop(qop);
            authorizationHeader.setCNonce(cNonce);
            authorizationHeader.setNonceCount(1);
        }
        registerRequest.addHeader(authorizationHeader);
        return registerRequest;
    }


    /**
     * 创建消息
     * @param device
     * @param context 请求体
     * @param viaTag
     * @param fromTag
     * @param toTag
     * @param callIdHeader
     * @return
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws PeerUnavailableException
     */
    public Request createKeetpaliveMessageRequest(Device device, String context, String viaTag, String fromTag, String toTag, CallIdHeader callIdHeader) throws ParseException, InvalidArgumentException, PeerUnavailableException {
        Request request = null;

        String sipServerAddr = sipConfig.getIp() + ":" + sipConfig.getPort();
        String deviceAddr = device.getDeviceIp() + ":" + device.getDevicePort();

        // sipuri
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipServerAddr);
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(device.getDeviceIp(), device.getDevicePort(),
                device.getRegisterProtocol(), viaTag);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(device.getDeviceId(), deviceAddr);
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        // to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(sipConfig.getId(), sipServerAddr);
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, toTag);
        // Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);
        request = sipFactory.createMessageFactory().createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        List<String> agentParam = new ArrayList<>();
        //agentParam.add("my-client");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("Application", "MANSCDP+xml");
        request.setContent(context, contentTypeHeader);
        return request;
    }


    /***
     * 回复状态码
     * 100 trying
     * 200 OK
     * 400
     * 404
     * @param evt
     * @throws SipException
     * @throws InvalidArgumentException
     * @throws ParseException
     */
    public void responseAck(RequestEvent evt, int statusCode) throws SipException, InvalidArgumentException, ParseException {
        Response response = sipFactory.createMessageFactory().createResponse(statusCode, evt.getRequest());
        ServerTransaction serverTransaction = getServerTransaction(evt);
        log.info("回复200 ok消息:{}", response);
        serverTransaction.sendResponse(response);
        if (statusCode >= 200 && !"NOTIFY".equals(evt.getRequest().getMethod())) {
            if (serverTransaction.getDialog() != null) serverTransaction.getDialog().delete();
        }
    }

    public void responseAck(RequestEvent evt, int statusCode, String msg) throws SipException, InvalidArgumentException, ParseException {
        Response response = sipFactory.createMessageFactory().createResponse(statusCode, evt.getRequest());
        response.setReasonPhrase(msg);
        ServerTransaction serverTransaction = getServerTransaction(evt);
        log.info("回复200 ok消息:{}", response);
        serverTransaction.sendResponse(response);
        if (statusCode >= 200 && !"NOTIFY".equals(evt.getRequest().getMethod())) {
            if (serverTransaction.getDialog() != null) serverTransaction.getDialog().delete();
        }
    }

    /**
     * 回复带sdp的200
     *
     * @param evt
     * @param sdp
     * @throws SipException
     * @throws InvalidArgumentException
     * @throws ParseException
     */
    public void responseAck(RequestEvent evt, String sdp) throws SipException, InvalidArgumentException, ParseException {
        Response response = sipFactory.createMessageFactory().createResponse(Response.OK, evt.getRequest());
        SipFactory sipFactory = SipFactory.getInstance();
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("APPLICATION", "SDP");
        response.setContent(sdp, contentTypeHeader);

        SipURI sipURI = (SipURI) evt.getRequest().getRequestURI();

        Address concatAddress = sipFactory.createAddressFactory().createAddress(
                sipFactory.createAddressFactory().createSipURI(sipURI.getUser(), sipURI.getHost() + ":" + sipURI.getPort()
                ));
        response.addHeader(sipFactory.createHeaderFactory().createContactHeader(concatAddress));
        log.info("回复ack消息：/n{}",response);
        getServerTransaction(evt).sendResponse(response);
    }


}
