帖子回复的格式设计:

```json
{
	"post": { // discussPost 对象
		// id, title, content ...
	},

	"user": { // post对应的用户
		// id, username, ...
	},
	"page": { // 评论的分页信息
		// from, to, current, rows, offset, total(页数)
	},
	"comments": [ // 该post的评论列表
		{ // comment 1
			"replyCount": 10, // 回复数量
			"commentContent": { // comment对象
				// id, content, target ...
			},
			"user": { // 发表该评论的用户
				// id, username, ...
			},
			"replys": [ // 针对该评论的回复列表
				{ // 回复1
					"replyContent": { // 回复对象
						// id, content, ...
					},
					"user": { // 发表回复的用户
						// id, username, ...
					},
					"target": { // 回复针对的用户
						// id, username, ...
					}
				}, 
				{ // 回复2

				}
			]
		},

		{ // comment 2

		},

	]

}
```