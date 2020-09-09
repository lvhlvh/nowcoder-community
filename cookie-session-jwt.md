* [Cookie](#cookie)
  * [为什么需要Cookie](#%E4%B8%BA%E4%BB%80%E4%B9%88%E9%9C%80%E8%A6%81cookie)
  * [Cookie的重要属性](#cookie%E7%9A%84%E9%87%8D%E8%A6%81%E5%B1%9E%E6%80%A7)
  * [Cookie的特点](#cookie%E7%9A%84%E7%89%B9%E7%82%B9)
  * [Cookie的失效时间](#cookie%E7%9A%84%E5%A4%B1%E6%95%88%E6%97%B6%E9%97%B4)
  * [Cookie的安全问题](#cookie%E7%9A%84%E5%AE%89%E5%85%A8%E9%97%AE%E9%A2%98)
    * [XSS](#xss)
    * [CSRF](#csrf)
* [Session](#session)
  * [什么是Session](#%E4%BB%80%E4%B9%88%E6%98%AFsession)
  * [Session的失效时间](#session%E7%9A%84%E5%A4%B1%E6%95%88%E6%97%B6%E9%97%B4)
  * [Cookie和Session的对比](#cookie%E5%92%8Csession%E7%9A%84%E5%AF%B9%E6%AF%94)
  * [分布式环境下的Session共享问题](#%E5%88%86%E5%B8%83%E5%BC%8F%E7%8E%AF%E5%A2%83%E4%B8%8B%E7%9A%84session%E5%85%B1%E4%
    BA%AB%E9%97%AE%E9%A2%98)
* [JWT/Token](#jwttoken)
  * [Token的大致原理](#token%E7%9A%84%E5%A4%A7%E8%87%B4%E5%8E%9F%E7%90%86)
  * [JWT的工作原理](#jwt%E7%9A%84%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86)
  * [消息验证码和签名](#%E6%B6%88%E6%81%AF%E9%AA%8C%E8%AF%81%E7%A0%81%E5%92%8C%E7%AD%BE%E5%90%8D)
  * [JWT的特点](#jwt%E7%9A%84%E7%89%B9%E7%82%B9)
  * [如何让JWT“失效”？](#%E5%A6%82%E4%BD%95%E8%AE%A9jwt%E5%A4%B1%E6%95%88)
* [一些问题](#%E4%B8%80%E4%BA%9B%E9%97%AE%E9%A2%98)
  * [使用 cookie 时需要考虑的问题](#%E4%BD%BF%E7%94%A8-cookie-%E6%97%B6%E9%9C%80%E8%A6%81%E8%80%83%E8
    %99%91%E7%9A%84%E9%97%AE%E9%A2%98)
  * [使用 session 时需要考虑的问题](#%E4%BD%BF%E7%94%A8-session-%E6%97%B6%E9%9C%80%E8%A6%81%E8%80%83%
    E8%99%91%E7%9A%84%E9%97%AE%E9%A2%98)
  * [使用 token 时需要考虑的问题](#%E4%BD%BF%E7%94%A8-token-%E6%97%B6%E9%9C%80%E8%A6%81%E8%80%83%E8%9
    9%91%E7%9A%84%E9%97%AE%E9%A2%98)
  * [使用 JWT 时需要考虑的问题](#%E4%BD%BF%E7%94%A8-jwt-%E6%97%B6%E9%9C%80%E8%A6%81%E8%80%83%E8%99%91
    %E7%9A%84%E9%97%AE%E9%A2%98)
  * [使用加密算法时需要考虑的问题](#%E4%BD%BF%E7%94%A8%E5%8A%A0%E5%AF%86%E7%AE%97%E6%B3%95%E6%97%B6%E
    9%9C%80%E8%A6%81%E8%80%83%E8%99%91%E7%9A%84%E9%97%AE%E9%A2%98)
* [参考](#%E5%8F%82%E8%80%83)

# Cookie 

## 为什么需要Cookie

我们知道HTTP协议本身是无状态的，也就是说服务器无法记录每个用户的状态。当客户端首次请求一个网站时，网站相应的HTTP头部会有一个`set-cookie`字段，给客户端设置cookie。之后客户端对该网站的每次http请求都会携带这个cookie，用于服务器识别用户的状态。

> :warning: ​一个Set-Cookie只能设置一个cookie键值对，要设置多个，需要使用多个Set-Cookie头部。

## Cookie的重要属性

- **name=value**：键值对，非ASCII字符需要编码，可以是UTF-8编码、Base64编码等等，总之客户端和服务器端约定好了就可以
- **domain、path**：域名和路径
- **maxAge、expires**：都和超时时间有关。
  - maxAge默认为负数，表示关闭浏览器后失效
  - maxAge=0表示删除该Cookie
- **secure**：为true标识cookie仅在HTTPS中有效
- **httpOnly**：表示不能通过JS读取cookie，可一定程度上防止**XSS攻击** (信息也可能存在localStorage等地方，因此不能完全防止)。

## Cookie的特点

- cookie的**大小限制**：Cookie存储在客户端，因为每次进行HTTP请求都会携带Cookie，所以Cookie不能太大，单个Cookie最大为4KB。
- cookie的**编码**：cookie的键值对可以直接存储除了`,`, `;`和`空格`之外的可打印ASCII字符，其他字符需要编码后存储
- cookie 遵循**同源策略**：所谓同源，就是**协议、域名(一级域名、二级域名...)、端口号** 3者都相同。
  - **默认**只有同源网站之间才能互相访问Cookie。
  - 如果要想在二级域名不同的网站之间之间共享Cookie，可以通过Cookie的**domain属性**配置。

## Cookie的失效时间

- 没设置maxAge或expires，关闭浏览器后失效
- 如果通过maxAge或expires设置了超时时间，则超时后失效

## Cookie的安全问题

### XSS

原因：服务端没对用户输入进行转义和过滤

对策：对用户输入进行转义和过滤

### CSRF

对策：

- 添加验证码
- 利用HTTP头部的Referer字段（但是可以被抓包工具篡改）
- CSRF_TOKEN。每次请求多出一个参数csrf_token，该token由服务器生成，每次请求都不一样。攻击者是无法预测这个token的。



# Session

## 什么是Session

除了可以将用户信息通过 Cookie 存储在用户浏览器中，也可以利用 Session 存储在服务器端，存储在服务器端的信息更加安全。

Session 可以存储在服务器上的**文件、数据库或者内存**中。也可以将 Session 存储在 Redis 这种**内存型数据库**中，效率会更高。

Session一般是**基于Cookie**实现的，需要在客户端的Cookie中存储SessionId。

## Session的失效时间

1. 服务器关闭后

2. 直接对session对象调用`invalidate()`

3. session**默认失效时间**达到。

   默认失效时间可以在tomcat目录下的`web.xml`文件中进行配置（默认30分钟）

   ```xml
   <session-config>
       <session-timeout>30</session-timeout>
   </session-config>
   ```

## Cookie和Session的对比

- Cookie只能存储除了`,`，`;`，`空格`之外的可打印ASCII字符；而Session可以存储任意类型的数据
- Session存储在服务器端，相对Cookie更安全些。如果非要在Cookie中存储隐私信息，可以进行加密。
- 对于大型网站，如果用户所有的信息都存储在 Session 中，那么开销是非常大的，因此不建议将所有的用户信息都存储到 Session 中。

## 分布式环境下的Session共享问题

> [分布式架构下，Session 共享有什么方案？](https://www.lagou.com/lgeduarticle/78783.html)
>
> [How to manage sessions in a distributed application](https://stackoverflow.com/questions/32688648/how-to-manage-sessions-in-a-distributed-application)

在分布式环境下，一个用户对同一网站的多次请求可能落到不同的服务器上，如何让这些服务器都能分辨出用户的状态呢？有如下解决方案：

- 采用token/jwt等代替session
- 配置不同的服务器之间进行session同步，存在延迟或同步失败问题
- 使用 Nginx （或其他复杂均衡软硬件）中的 **IP 绑定策略**，同一个 IP 只能在指定的同一个机器访问，但是这样做失去了负载均衡的意义，**当挂掉一台服务器的时候，会影响一批用户的使用**，风险很大；
- 把Session存到Redis等内存型数据库中
- 将Session存储在数据库中，确保持久化

# JWT/Token

## Token的大致原理

![](https://raw.githubusercontent.com/lvhlvh/pictures/master/img/20200908111824.png)

如图所示，相比于Session，Token的验证方法中，只需要客户端存储Token即可，**服务端无需存储Token，只需要验证客户端Token的合法性即可**。所以，相比于Session，它用计算换取了空间上的效率。

## JWT的工作原理

> [JSON Web Token 入门教程](http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html)

JWT即Json Web Token，JWT可以用作Token。

JWT分为三部分：`Header.Payload.Signature`

**Header**是一个**JSON对象**，采用**Base64**进行编码，包含签名/消息验证码算法alg和令牌类型typ。

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

> 对于JWT，令牌类型固定为JWT，除了JWS、JWE等等，暂不讨论

**Payload**部分也是一个**JSON对象**，采用**Base64**进行编码，包含一系列的claim，例如签发人、过期时间、主体(subject)等等，还可以自定义一些claim。我们可以使用payload存放用户id等标识用户身份的信息，但是需要注意，payload的内容默认是没有加密的，不适合存放隐私的信息。

**Signature**部分是对Header和Payload部分计算出的**签名或消息验证码**，可以采用HMAC消息验证码算法，也可以使用RSA等公钥密码算法。这些算法可以**验证消息来源**并**确保消息完整性**：

```bash
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```



![](https://raw.githubusercontent.com/lvhlvh/pictures/master/img/20200908161408.png)

JWT的**工作流程**如下：

- 服务端选定密钥，选定要封装到payload中的信息，然后计算jwt token，返回给客户端
- 客户端收到JWT后，可以存在Cookie中，也可存在localStorage中
- 客户端如果要给服务器发送JWT的话，可以通过Cookie，也可通过HTTP头部的`Authorization: Bearer <token>`字段发送，后者的好处是**支持跨域**。
- 服务器收到客户端的JWT后，使用之前的秘钥对JWT进行验证，验证通过则给予用户访问权限。

## 消息验证码和签名

**消息验证码**：验证消息来源、完整性

**签名**：验证消息来源、不可否认性、完整性

## JWT的特点

- **和session的对比**：JWT只需要存储在客户端即可，不需要存储在服务器端，可以节省服务器端的空间；也无需像session那样考虑共享问题
- **失效问题**：JWT是无状态的，也就是无需服务器端存储会话信息。但是这也带来了一个明显的缺点，服务器一旦签发了JWT，在超时之前就无法对它进行修改或废弃，这样一旦JWT被攻击者窃取，就会带来严重的安全问题。
- **payload不加密**：JWT的payload默认不加密，因此不适合存储隐私信息
- **CSRF攻击**：可以一定程度上避免CSRF攻击
- **跨域问题**：JWT支持跨域，因此对于实现单点登录系统比较友好。

## 如何让JWT“失效”？

- 客户端把JWT删除
- 设置较短的失效时间
- 服务器创建一个token黑名单

# 一些问题

关键词：跨域、加密、XSS、CSRF、共享、占用空间大小、失效

### **使用 cookie 时需要考虑的问题**

- 因为存储在客户端，容易被客户端篡改，使用前需要验证合法性；
- 不要存储敏感数据，比如用户密码，账户余额；
- XSS攻击问题，httpOnly 在一定程度上提高安全性；
- 尽量减少 cookie 的体积，能存储的数据量不能超过 4kb；
- 设置正确的 domain 和 path，减少数据传输；
- cookie 无法跨域；
- 一个浏览器针对一个网站最多存 20 个Cookie，浏览器一般只允许存放 300 个Cookie；
- 移动端对 cookie 的支持不是很好，而 session 需要基于 cookie 实现，所以移动端常用的是 token；

### **使用 session 时需要考虑的问题**

- 将 session 存储在服务器里面，当用户同时在线量比较多时，这些 session 会占据较多的内存，需要在服务端定期的去清理过期的 session；
- session共享问题
- 当多个应用要共享 session 时，除了以上问题，还会遇到跨域问题，因为不同的应用可能部署的主机不一样，需要在各个应用做好 cookie 跨域的处理；
- sessionId 是存储在 cookie 中的，假如浏览器禁止 cookie 或不支持 cookie 怎么办？ 一般会把 sessionId 跟在 url 参数后面即重写 url，所以 session 不一定非得需要靠 cookie 实现；
- 移动端对 cookie 的支持不是很好，而 session 需要基于 cookie 实现，所以移动端常用的是 token；

### **使用 token 时需要考虑的问题**

- 如果你认为用数据库来存储 token 会导致查询时间太长，可以选择放在内存当中。比如 redis 很适合你对 token 查询的需求；
- token 完全由应用管理，所以它可以避开同源策略；
- token 可以避免 CSRF 攻击(因为不需要 cookie 了)；
- 移动端对 cookie 的支持不是很好，而 session 需要基于 cookie 实现，所以移动端常用的是 token；

### **使用 JWT 时需要考虑的问题**

- 因为 JWT 并不依赖 Cookie 的，所以你可以使用任何域名提供你的 API 服务而不需要担心跨域资源共享问题（CORS）；
- JWT 默认是不加密，但也是可以加密的。生成原始 Token 以后，可以用密钥再加密一次；
- JWT 不加密的情况下，不能将秘密数据写入 JWT；
- JWT 不仅可以用于认证，也可以用于交换信息。有效使用 JWT，可以降低服务器查询数据库的次数；
- JWT 最大的优势是服务器不再需要存储 Session，使得服务器认证鉴权业务可以方便扩展。但这也是 JWT 最大的缺点：由于服务器不需要存储 Session 状态，因此使用过程中无法废弃某个 Token 或者更改 Token 的权限。也就是说一旦 JWT 签发了，到期之前就会始终有效，除非服务器部署额外的逻辑；
- JWT 本身包含了认证信息，一旦泄露，任何人都可以获得该令牌的所有权限。为了减少盗用，JWT的有效期应该设置得比较短。对于一些比较重要的权限，使用时应该再次对用户进行认证；
- JWT 适合一次性的命令认证，颁发一个有效期极短的 JWT，即使暴露了危险也很小，由于每次操作都会生成新的 JWT，因此也没必要保存 JWT，真正实现无状态；
- 为了减少盗用，JWT 不应该使用 HTTP 协议明码传输，要使用 HTTPS 协议传输；

### **使用加密算法时需要考虑的问题**

- 绝不要以明文存储密码；
- 永远使用 哈希算法 来处理密码，绝不要使用 Base64 或其他编码方式来存储密码，这和以明文存储密码是一样的，使用哈希，而不要使用编码。编码以及加密，都是双向的过程，而密码是保密的，应该只被它的所有者知道， 这个过程必须是单向的。哈希正是用于做这个的，从来没有解哈希这种说法， 但是编码就存在解码，加密就存在解密；
- 绝不要使用弱哈希或已被破解的哈希算法，像 MD5 或 SHA1 ，只使用强密码哈希算法；
- 绝不要以明文形式显示或发送密码，即使是对密码的所有者也应该这样。如果你需要 “忘记密码” 的功能，可以随机生成一个新的 一次性的（这点很重要）密码，然后把这个密码发送给用户；

# 参考

[1] SESSION、TOKEN与JWT安全解析

[2] 关于 Cookie、Session、Token、JWT 会被问到的一切！

[3] [聊一聊Cookie](https://segmentfault.com/a/1190000004556040)

[4] [浏览器同源政策及其规避方法](https://www.ruanyifeng.com/blog/2016/04/same-origin-policy.html)

[5] [JWT 身份认证优缺点分析以及常见问题解决方案](https://zhuanlan.zhihu.com/p/85873228)